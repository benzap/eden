(ns eden.core
  (:refer-clojure :exclude [eval])
  (:require
   [eden.state-machine :refer [new-state-machine]]
   [eden.std.ast :refer [astm]]
   [eden.std.display :refer [display-node]]
   [eden.stdlib :refer [import-stdlib]]
   [eden.def]))


(declare set-var!)


(defn new-eden-instance []
  (let [*sm (atom (new-state-machine))]
    {:*sm *sm
     :astm (astm *sm)}))


(defn eden []
  (let [eden-instance (new-eden-instance)]
    (import-stdlib eden-instance)
    eden-instance))


(def ^:dynamic *default-eden-instance* (eden))


(defn reset-instance! []
  (reset! (:*sm *default-eden-instance*) (new-state-machine))
  (import-stdlib *default-eden-instance*)
  nil)


(defn set-var!
  [identifier value]
  (swap! (:*sm *default-eden-instance*) eden.state-machine/set-global-var identifier value)
  nil)


(defn set-function! [identifier value]
  (eden.def/set-function! *default-eden-instance* identifier value)
  nil)


(def wrap-function eden.def/wrap-function)


(defn get-var
  [identifier]
  (eden.state-machine/get-var
   (-> *default-eden-instance* :*sm deref) identifier))


(defmacro with-eden-instance [instance & body]
  `(binding [*default-eden-instance* ~instance]
    ~@body))


(defn eval-expression-fn [tokens]
  (eden.std.ast/evaluate-expression (:astm *default-eden-instance*) tokens))


;; (eval-expression-fn '[4 * 2])


(defmacro eval-expression
  [& tokens]
  `(eval-expression-fn (vec (quote ~tokens))))


;; (eval-expression 2 + 2 * 4)


(defn parse-fn [tokens]
  (let [stmts (eden.std.ast/parse (:astm *default-eden-instance*) tokens)]
    (mapv display-node stmts)))


(defmacro parse [& tokens]
  `(parse-fn (vec (quote ~tokens))))


;; (parse 2 + 2)


(defn eval-fn [tokens]
  (eden.std.ast/evaluate (:astm *default-eden-instance*) tokens))


(defmacro eval [& tokens]
  `(eval-fn (vec (quote ~tokens))))


;;(eval print "Hello World!")


#_(eval-expression
   10 != 12 and "Yes" or "No")

#_(eval
   print("Test")
   x = 2 + 2 * 4
   print(x))

#_(get-var 'x)


#_(eval-expression
   4 - 2 + 2)


#_(eval
   x = {:value 12}
   print x.value) ;; 12