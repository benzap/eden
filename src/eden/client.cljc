(ns eden.client
  "Functions for better interoperability between eden and a
  clojure(script) client."
  (:require [clojure.string :as str]))


(def ^:dynamic *eden-clojure-value-escape* '%clj)


(defmacro form
  "quoted form with escaped evaluation. Values preceding
  *eden-clojure-value-escape* are evaluated in clojure(script).
  Examples:
  ;; Assuming we want to pull a clojure value into the a quoted form
  (def x 10)
  (form value %= x) ;; => '[form value 10]
  Notes:
  - *eden-clojure-value-escape can be replaced with a different escape
  symbol as desired."
  [& body]
  (:result
    (reduce
      (fn [{:keys [result eval-next?]} atom]
        (cond
          (= atom *eden-clojure-value-escape*)
          {:result result
           :eval-next? true}
          eval-next?
          {:result (conj result atom)}
          :else
          {:result (conj result `(quote ~atom))}))
      {:result []}
      body)))


(defmacro form-string
  "Equivlant to `form`, but presents the result as a string that can
  be consumed by a eden state machine."
  [& body]
  `(let [sform# (pr-str (form ~@body))]
     ;; Remove surrounding vector
     (subs sform# 1 (dec (count sform#)))))
