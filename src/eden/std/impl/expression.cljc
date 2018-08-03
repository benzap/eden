(ns eden.std.impl.expression
  (:require
   [eden.state-machine :as state]
   [eden.std.display :as display :refer [display-node]]
   [eden.std.exceptions :refer [runtime-error]]
   [eden.std.expression :refer [Expression
                                evaluate-expression
                                EXPRESSION##]]
   [eden.std.token :refer [TokenType]]
   [eden.std.meta :as std.meta]))


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
      (and left right)))

  display/Display
  (display-node [_]
   (str "(and " (display-node left) " " (display-node right) ")")))


(defrecord OrExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)
  
  Expression
  (evaluate-expression [_]
    (let [left (evaluate-expression left)
          right (evaluate-expression right)]
      (or left right)))

  display/Display
  (display-node [_]
    (str "(or " (display-node left) " " (display-node right) ")")))


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
      (= left right)))

  display/Display
  (display-node [_]
    (str "(= " (display-node left) " " (display-node right) ")")))


(defrecord NonEqualityExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (not=
     (evaluate-expression left)
     (evaluate-expression right)))

  display/Display
  (display-node [_]
    (str "(not= " (display-node left) " " (display-node right) ")")))

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
     (evaluate-expression right)))

  display/Display
  (display-node [_]
    (str "(> " (display-node left) " " (display-node right) ")")))


(defrecord GreaterThanOrEqualExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (>=
     (evaluate-expression left)
     (evaluate-expression right)))

  display/Display
  (display-node [_]
    (str "(>= " (display-node left) " " (display-node right) ")")))


(defrecord LessThanExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (<
     (evaluate-expression left)
     (evaluate-expression right)))

  display/Display
  (display-node [_]
    (str "(< " (display-node left) " " (display-node right) ")")))


(defrecord LessThanOrEqualExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (<=
     (evaluate-expression left)
     (evaluate-expression right)))

  display/Display
  (display-node [_]
    (str "(<= " (display-node left) " " (display-node right) ")")))


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
     (evaluate-expression right)))

  display/Display
  (display-node [_]
    (str "(+ " (display-node left) " " (display-node right) ")")))


(defrecord SubtractionExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (-
     (evaluate-expression left)
     (evaluate-expression right)))

  display/Display
  (display-node [_]
    (str "(- " (display-node left) " " (display-node right) ")")))


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
     (evaluate-expression right)))

  display/Display
  (display-node [_]
    (str "(* " (display-node left) " " (display-node right) ")")))


(defrecord DivisionExpression [left right]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (/
     (evaluate-expression left)
     (evaluate-expression right)))

  display/Display
  (display-node [_]
    (str "(/ " (display-node left) " " (display-node right) ")")))


;;
;; Unary / Not / Negation
;;


(defrecord NotExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (not (evaluate-expression value)))

  display/Display
  (display-node [_]
    (str "(not " (display-node value) ")")))


(defrecord NegationExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (- (evaluate-expression value)))

  display/Display
  (display-node [_]
    (str "(- " (display-node value) ")")))


;;
;; Primary Values
;;


(defrecord StringExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] value)

  display/Display
  (display-node [_] (str "\"value\"")))


(defrecord KeywordExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] value)

  display/Display
  (display-node [_] (str value)))


(defrecord FloatExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] value)

  display/Display
  (display-node [_] (str value)))


(defrecord IntegerExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] value)

  display/Display
  (display-node [_] (str value)))


(defrecord BooleanExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] value)

  display/Display
  (display-node [_] (str value)))


(defrecord NullExpression []
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_] nil)

  display/Display
  (display-node [_] (str nil)))


(defrecord VectorExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (vec (for [expr value] (evaluate-expression expr))))

  display/Display
  (display-node [_]
    (loop [s "[" v value]
      (if-not (empty? v)
        (recur (str s (display-node (first v)) " ") (rest v))
        (str s "]")))))


(defrecord HashMapExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (into {} (for [[k v] value]
               [(evaluate-expression k)
                (evaluate-expression v)])))

  display/Display
  (display-node [_]
    (loop [s "{" m (into [] value)]
      (if-not (empty? m)
        (let [[k v] (first m)]
          (recur (str s (display-node k) " " (display-node v) "\n") (rest m)))
        (str s "}")))))


(defrecord HashSetExpression [value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (set (for [expr value] (evaluate-expression expr))))

  display/Display
  (display-node [_]
    (loop [s "#{" v (into [] value)]
      (if-not (empty? v)
        (recur (str s (display-node (first v)) " ") (rest v))
        (str s "}")))))


(defrecord IdentifierExpression [*sm value]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (state/get-var @*sm value))

  display/Display
  (display-node [_] (str value)))


(defrecord CallFunctionExpression [*sm expr arg-exprs]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (let [fcn (evaluate-expression expr)
          args (for [arg-expr arg-exprs]
                 (evaluate-expression arg-expr))]
      (cond
        (std.meta/eden-callable? fcn)
        (std.meta/__call fcn args)

        (or (fn? fcn) (ifn? fcn))
        (apply fcn args)

        :else
        (runtime-error (str "Given expression value is not callable. '" fcn "'")))))

  display/Display
  (display-node [_]
    (loop [s (str "(" (display-node expr)) arg-exprs arg-exprs]
      (if-not (empty? arg-exprs)
        (recur (str s " " (display-node (first arg-exprs))) (rest arg-exprs))
        (str s ")")))))


(defrecord GetPropertyExpression [key-expr expr]
  TokenType
  (token-type [_] EXPRESSION##)

  Expression
  (evaluate-expression [_]
    (let [key (evaluate-expression key-expr)
          val (evaluate-expression expr)]
      (get val key)))

  display/Display
  (display-node [_]
    (str "(get " (display-node expr) " " (display-node key-expr) ")")))
