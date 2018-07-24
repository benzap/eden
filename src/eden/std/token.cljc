(ns eden.std.token
  (:require
   [eden.std.reserved :refer [reserved?]]
   [eden.utils.symbol :as symbol :refer [starts-with?]]))


(defn identifier? [sym]
  (and
   (symbol? sym)
   (not (reserved? sym))
   (not (symbol/contains? sym '.))))


(defprotocol TokenType
  (token-type [this]
    "Returns a ns-keyword with the type of token ie. ::expression,
    or ::statement."))


(defn identifier-assoc?
  "Determines if it is an assoc identifier, which consists of dot
  notation and vector notation."
  [sym]
  (or
   (and
     (symbol? sym)
     (not (reserved? sym))
     (symbol/contains? sym '.)
     (not (symbol/contains? sym '..)))
   (vector? sym)))


#_(identifier-assoc? 'hello..there)
  
(defn dot-assoc->keyword-list [sym]
  (->> (symbol/split (symbol/stripl sym '.) #"\.")
       (mapv keyword)))


#_(dot-assoc->keyword-list 'hello.there)
