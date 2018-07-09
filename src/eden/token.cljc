(ns eden.token
  (:require
   [eden.state-machine :as state]))


(defprotocol TokenType
  (token-type [this]
    "Returns a ns-keyword with the type of token ie. ::expression,
    or ::statement."))


(defprotocol Expression
  (evaluate-expression [this]
    "Evaluates the expression, usually in a recursive manner"))


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
