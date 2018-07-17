(ns eden.std.statement
  (:require
   [eden.std.token :refer [TokenType token-type]]))


(defprotocol Statement
  (evaluate-statement [this]))


(def STATEMENT## ::statement)
(defn isa-statement? [obj]
  (when (satisfies? TokenType obj)
    (= STATEMENT## (token-type obj))))


