(ns eden.core
  (:refer-clojure :exclude [eval])
  (:require
   [eden.state-machine :refer [new-state-machine]]
   [eden.std.ast :refer [astm]]))


(defn eden []
  (let [*sm (atom (new-state-machine))]
    {:*sm *sm
     :astm (astm *sm)}))


(def ^:dynamic *default-eden-instance* (eden))


(defn reset-instance! []
  (reset! (:*sm *default-eden-instance*) (new-state-machine)))


(defn set-var!
  [identifier value]
  (swap! (:*sm eden) eden.state-machine/set-global-var identifier value))


(defn get-var
  [identifier]
  (eden.state-machine/get-var (-> *default-eden-instance* :*sm deref) identifier))


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
  (eden.std.ast/parse (:astm *default-eden-instance*) tokens))


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
