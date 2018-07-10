(ns eden.core
  (:refer-clojure :exclude [eval])
  (:require
   [eden.state-machine :refer [new-state-machine]]
   [eden.ast :refer [astm]]))


(defn eden []
  (let [*sm (atom (new-state-machine))]
    {:*sm *sm
     :astm (astm *sm)}))


(defn eval-expression-fn [tokens]
  (let [eden (eden)]
    (eden.ast/evaluate-expression (:astm eden) tokens)))


;; (eval-expression-fn '[4 * 2])


(defmacro eval-expression
  [& tokens]
  `(eval-expression-fn (vec (quote ~tokens))))


;; (eval-expression 2 + 2 * 4)


(defn parse-fn [tokens]
  (let [eden (eden)]
    (eden.ast/parse (:astm eden) tokens)))


(defmacro parse [& tokens]
  `(parse-fn (vec (quote ~tokens))))


;; (parse 2 + 2)


(defn eval-fn [tokens]
  (let [eden (eden)]
    (eden.ast/evaluate (:astm eden) tokens)))


(defmacro eval [& tokens]
  `(eval-fn (vec (quote ~tokens))))


;;(eval print "Hello World!")


#_(eval-expression
   10 != 12 and "Yes" or "No")

#_(eval
   print("Test"))
