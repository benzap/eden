(ns eden.token)


(defprotocol TokenType
  (token-type [this]
    "Returns a ns-keyword with the type of token ie. ::expression,
    or ::statement."))


(defprotocol Expression
  (evaluate [this]
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
  (evaluate [_] (not-implemented)))


(defrecord NonEqualityExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


;;
;; Comparison
;;


(defrecord GreaterThanExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


(defrecord GreaterThanOrEqualExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


(defrecord LessThanExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


(defrecord LessThanOrEqualExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


;;
;; Addition/Subtraction
;;


(defrecord AdditionExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


(defrecord SubtractionExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


;;
;; Multiplication/Division
;;


(defrecord MultiplicationExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


(defrecord DivisionExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


;;
;; Unary/Not/Negation
;;


(defrecord NotExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


(defrecord NegationExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


;;
;; Primary Values
;;


(defrecord StringExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] value))


(defrecord KeywordExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] value))


(defrecord FloatExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] value))


(defrecord IntegerExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] value))


(defrecord BooleanExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] value))


(defrecord NullExpression []
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] nil))


(defrecord VectorExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


(defrecord HashMapExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))


(defrecord HashSetExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate [_] (not-implemented)))
