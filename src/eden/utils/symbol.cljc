(ns eden.utils.symbol
  (:refer-clojure :exclude [contains?])
  (:require
   [clojure.string :as str]))


(defn starts-with?
  [sym s]
  (let [sym (str sym)
        s (str s)]
    (str/starts-with? sym s)))


(defn ends-with?
  [sym s]
  (let [sym (str sym)
        s (str s)]
    (str/ends-with? sym s)))


(defn contains?
  [sym s]
  (let [sym (str sym)
        s (str s)]
    (str/includes? sym s)))


(defn split
  [sym s]
  (let [sym (str sym)]
    (->> (str/split sym s) (map symbol) vec)))
  

(defn stripl
  [sym s]
  (let [sym (str sym)
        s (str s)]
    (if (str/starts-with? sym s)
      (symbol (subs sym 1))
      (symbol sym))))
    
