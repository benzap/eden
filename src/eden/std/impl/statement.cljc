(ns eden.std.impl.statement
  (:require
   [eden.state-machine :as state]
   [eden.state-machine.environment :refer [with-new-environment]]
   [eden.std.statement :refer [Statement
                               evaluate-statement
                               STATEMENT##]]
   [eden.std.expression :refer [evaluate-expression]]
   [eden.std.token :refer [TokenType token-type]]
   [eden.std.return :as std.return]))


(defrecord PrintStatement [expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (println (evaluate-expression expr))))


(defrecord ExpressionStatement [expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (evaluate-expression expr)))


(defrecord DeclareVariableStatement [*sm var expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (let [value (evaluate-expression expr)]
      (swap! *sm state/set-var var value))))


(defrecord DeclareLocalVariableStatement [*sm var expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (let [value (evaluate-expression expr)]
      (swap! *sm state/set-local-var var value))))


(defrecord IfConditionalStatement [*sm conditional-expr truthy-stmts falsy-stmts]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (let [value (evaluate-expression conditional-expr)]
      (with-new-environment *sm
        (if value
          (doseq [stmt truthy-stmts]
            (evaluate-statement stmt))

          (doseq [stmt falsy-stmts]
            (evaluate-statement stmt)))))))


(defrecord WhileStatement [*sm conditional-expr stmts]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (while (evaluate-expression conditional-expr)
      (with-new-environment *sm
        (doseq [stmt stmts]
          (evaluate-statement stmt))))))


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
          (evaluate-statement stmt))))))


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
        (recur (+ idx step))))))


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
      (throw (Throwable. "Invalid collection in for-each statement.")))))


(defrecord ReturnStatement [expr]
  TokenType
  (token-type [_] STATEMENT##)

  Statement
  (evaluate-statement [_]
    (std.return/throw-return-value (evaluate-expression expr))))
