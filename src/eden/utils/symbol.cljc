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
    (loop [sym sym]
      (if (str/starts-with? sym s)
        (recur (subs sym 1))
        (symbol sym)))))


(defn stripl1
  [sym s]
  (let [sym (str sym)
        s (str s)]
    (if (str/starts-with? sym s)
      (symbol (subs sym (count s)))
      (symbol sym))))


(defn regex->str [r]
  (-> r
      str
      (str/replace #"\\" "")))



(defn starts-with-n?
  [sym s]
  (let [sym (str sym)
        s (str s)]
    (loop [sym sym n 0]
      (if (str/starts-with? sym s)
        (recur (subs sym 1) (inc n))
        n))))


(defn split-before-point
  "Splits a symbol into a vector of symbols with the given match
   ex. for splitting out dot-notation values"
  [sym match]
  (let [sym (str sym)
        smatch (regex->str match)
        started-with? (str/starts-with? sym smatch)
        slist (str/split sym match)]
    (loop [rlist (reverse slist) sym-list []]
      (if-not (empty? rlist)
        (let [x (first rlist)
              rlist (rest rlist)]
          (cond
            (empty? x)
            (recur rlist
                   (assoc-in sym-list [(dec (count sym-list))] (symbol (str smatch (last sym-list)))))
            
            :else
            (recur rlist
                   (conj sym-list (symbol (str smatch x))))))
        (let [sym-list
              (vec (reverse sym-list))]
          (assoc-in sym-list [0] (stripl1 (first sym-list) smatch)))))))


#_(split-before-point 'hello..there #"\.\.")


(defn split-dot-notation
  [sym]
  (split-before-point sym #"\."))
