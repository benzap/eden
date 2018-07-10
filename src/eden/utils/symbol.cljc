(ns eden.utils.symbol
  (:require
   [clojure.string :as str]))


(defn starts-with?
  [sym s]
  (let [sym (str sym)
        s (str s)]
    (str/starts-with? sym s)))
