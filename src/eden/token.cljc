(ns eden.token
  (:require
   [eden.state-machine :as state]
   [eden.reserved :refer [reserved?]]
   [eden.meta]))


(defmacro with-new-environment
  [*sm & body]
  `(do
     (swap! ~*sm state/add-environment)
     ~@body
     (swap! ~*sm state/remove-environment)))


(defn identifier? [sym]
  (and
   (symbol? sym)
   (not (reserved? sym))))


(defprotocol TokenType
  (token-type [this]
    "Returns a ns-keyword with the type of token ie. ::expression,
    or ::statement."))


(defprotocol Expression
  (evaluate-expression [this]))


(defprotocol Statement
  (evaluate-statement [this]))


(def EXPRESSION## ::expression)
(defn isa-expression? [obj]
  (when (satisfies? TokenType obj)
    (= EXPRESSION## (token-type obj))))


(def STATEMENT## ::statement)
(defn isa-statement? [obj]
  (when (satisfies? TokenType obj)
    (= STATEMENT## (token-type obj))))


(defn not-implemented
  ([msg]
   (throw (Throwable. (str "Not Implemented " msg))))
  ([] (not-implemented "")))


;;
;; Logical
;;


(defrecord AndExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)
  
  Expression
  (evaluate-expression [_]
    (let [left (evaluate-expression left)
          right (evaluate-expression right)]
      (and left right))))


(defrecord OrExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)
  
  Expression
  (evaluate-expression [_]
    (let [left (evaluate-expression left)
          right (evaluate-expression right)]
      (or left right))))


;;
;; Equality
;;


(defrecord EqualityExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)
  
  Expression
  (evaluate-expression [_]
    (let [left (evaluate-expression left)
          right (evaluate-expression right)]
      (= left right))))


(defrecord NonEqualityExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (not=
     (evaluate-expression left)
     (evaluate-expression right))))


;;
;; Comparison
;;


(defrecord GreaterThanExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (>
     (evaluate-expression left)
     (evaluate-expression right))))


(defrecord GreaterThanOrEqualExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (>=
     (evaluate-expression left)
     (evaluate-expression right))))


(defrecord LessThanExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (<
     (evaluate-expression left)
     (evaluate-expression right))))


(defrecord LessThanOrEqualExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (<=
     (evaluate-expression left)
     (evaluate-expression right))))


;;
;; Addition/Subtraction
;;


(defrecord AdditionExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (+
     (evaluate-expression left)
     (evaluate-expression right))))


(defrecord SubtractionExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (-
     (evaluate-expression left)
     (evaluate-expression right))))


;;
;; Multiplication/Division
;;


(defrecord MultiplicationExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (*
     (evaluate-expression left)
     (evaluate-expression right))))


(defrecord DivisionExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (/
     (evaluate-expression left)
     (evaluate-expression right))))


;;
;; Unary/Not/Negation
;;


(defrecord NotExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (not (evaluate-expression value))))


(defrecord NegationExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (- (evaluate-expression value))))


;;
;; Primary Values
;;


(defrecord StringExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] value))


(defrecord KeywordExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] value))


(defrecord FloatExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] value))


(defrecord IntegerExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] value))


(defrecord BooleanExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] value))


(defrecord NullExpression []
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] nil))


(defrecord VectorExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (vec (for [expr value] (evaluate-expression expr)))))


(defrecord HashMapExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (into {} (for [[k v] value]
               [(evaluate-expression k)
                (evaluate-expression v)]))))


(defrecord HashSetExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (set (for [expr value] (evaluate-expression expr)))))


(defrecord IdentifierExpression [*sm value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (state/get-var @*sm value)))


;;
;; Statements
;;


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


(defrecord CallFunctionExpression [*sm expr arg-exprs]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (let [fcn (evaluate-expression expr)
          args (for [arg-expr arg-exprs]
                 (evaluate-expression arg-expr))]
      (if (eden.meta/eden-callable? fcn)
        (eden.meta/__call fcn args)
        (throw (Throwable. "Given expression value is not callable."))))))
