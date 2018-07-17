(ns eden.std.expression
  (:require
   [eden.std.token :refer [TokenType token-type]]))


(defprotocol Expression
  (evaluate-expression [this]))


(def EXPRESSION## ::expression)
(defn isa-expression? [obj]
  (when (satisfies? TokenType obj)
    (= EXPRESSION## (token-type obj))))
