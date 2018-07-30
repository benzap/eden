(ns eden.std.impl.statement
  (:require
   [eden.state-machine :as state]
   [eden.state-machine.environment :refer [with-new-environment]]
   [eden.std.statement :refer [Statement
                               evaluate-statement
                               STATEMENT##]]
   [eden.std.expression :refer [Expression evaluate-expression]]
   [eden.std.exceptions :refer [parser-error]]
   [eden.std.display :as display :refer [display-node]]
   [eden.std.token :refer [TokenType token-type]]
   [eden.std.return :as std.return]
   [eden.std.export :as std.export]
   [eden.std.meta :as meta])
  #?(:clj (:import clojure.lang.ExceptionInfo)))


(defn display-do-statement [stmts]
  (loop [s (str "(do") stmts stmts]
    (if-not (empty? stmts)
      (recur (str s " " (display-node (first stmts))) (rest stmts))
      (str s ")"))))


(defrecord PrintStatement [expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (println (evaluate-expression expr)))

  display/Display
  (display-node [_]
    (str "(print " (display-node expr) ")")))


(defrecord ExpressionStatement [expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (evaluate-expression expr))

  display/Display
  (display-node [_] (display-node expr)))


(defrecord DeclareVariableStatement [*sm var expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (let [value (evaluate-expression expr)]
      (swap! *sm state/set-var var value)))

  display/Display
  (display-node [_]
    (str "(setq " var " " (display-node expr) ")")))


(defrecord DeclareLocalVariableStatement [*sm var expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (let [value (evaluate-expression expr)]
      (swap! *sm state/set-local-var var value)))

  display/Display
  (display-node [_]
    (str "(setl " var " " (display-node expr) ")")))


(defrecord IfConditionalStatement [*sm conditional-expr truthy-stmts falsy-stmts]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (let [value (evaluate-expression conditional-expr)]
      (if value
        (with-new-environment *sm
          (doseq [stmt truthy-stmts]
            (evaluate-statement stmt)))
        (with-new-environment *sm
          (doseq [stmt falsy-stmts]
            (evaluate-statement stmt))))))

  display/Display
  (display-node [_]
    (str "(if " (display-node conditional-expr) " "
         (display-do-statement truthy-stmts) " "
         (display-do-statement falsy-stmts) ")")))


(defrecord WhileStatement [*sm conditional-expr stmts]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (while (evaluate-expression conditional-expr)
      (with-new-environment *sm
        (doseq [stmt stmts]
          (evaluate-statement stmt)))))

  display/Display
  (display-node [_]
    (str "(while " (display-node conditional-expr) " "
         (display-do-statement stmts) ")")))


(defrecord RepeatStatement [*sm conditional-expr stmts]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (with-new-environment *sm
      (doseq [stmt stmts]
        (evaluate-statement stmt)))

    (while (not (evaluate-expression conditional-expr))
      (with-new-environment *sm
        (doseq [stmt stmts]
          (evaluate-statement stmt)))))

  display/Display
  (display-node [_]
    (str "(repeat " (display-do-statement stmts)
         " (until " (display-node conditional-expr) "))")))


(defrecord ForStatement [*sm iter-var start end step stmts]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (loop [idx start]
      (when (<= idx end)
        (with-new-environment *sm
          (swap! *sm state/set-local-var iter-var idx)
          (doseq [stmt stmts]
            (evaluate-statement stmt)))
        (recur (+ idx step)))))

  display/Display
  (display-node [_]
    (str "(for [" iter-var "(range " start " " end " " step ")]"
         (display-do-statement stmts) ")")))


(defrecord ForEachStatement [*sm iter-var coll-expr stmts]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (if-let [coll (evaluate-expression coll-expr)]
      ;; TODO: check if a collection
      (doseq [item coll]
        (with-new-environment *sm
          (swap! *sm state/set-local-var iter-var item)
          (doseq [stmt stmts]
            (evaluate-statement stmt))))
      (parser-error "Invalid collection in for-each statement.")))

  display/Display
  (display-node [_]
    (str "(for [" iter-var " " (display-node coll-expr) "]"
         (display-do-statement stmts) ")")))


(defrecord ReturnStatement [expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (std.return/throw-return-value (evaluate-expression expr)))

  display/Display
  (display-node [_]
    (str "(return " (display-node expr) ")")))


(defrecord AssociateChainStatement [*sm var expr-assoc-list expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (let [val (state/get-var @*sm var)
          evaluated-assoc-list (vec (for [expr expr-assoc-list] (evaluate-expression expr)))
          val (assoc-in val evaluated-assoc-list (evaluate-expression expr))]
      (swap! *sm state/set-var var val)))

  display/Display
  (display-node [_]
    (str "(get-in " (display-node expr) " <expr>)")))


(defrecord RequireModuleStatement [*sm spath stmts *export-value]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (binding [std.export/*enable-export?* true]
      (try
        (swap! *sm state/add-environment)
        (doseq [stmt stmts]
          (evaluate-statement stmt))

        (catch #?(:clj ExceptionInfo :cljs js/Object) ex
          (reset! *export-value (std.export/catch-export-value ex)))

        (finally
          (swap! *sm state/remove-environment)))))

  Expression
  (evaluate-expression [this]
    (evaluate-statement this)
    @*export-value)

  display/Display
  (display-node [_]
    (str "(require " spath ")")))


(defrecord ExportModuleStatement [expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (when std.export/*enable-export?*
      (std.export/throw-export-value (evaluate-expression expr))))

  display/Display
  (display-node [_]
    (str "(export " (display-node expr) ")")))
