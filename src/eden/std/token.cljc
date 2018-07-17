(ns eden.std.token
  (:require
   [eden.std.reserved :refer [reserved?]]))


(defn identifier? [sym]
  (and
   (symbol? sym)
   (not (reserved? sym))))


(defprotocol TokenType
  (token-type [this]
    "Returns a ns-keyword with the type of token ie. ::expression,
    or ::statement."))
