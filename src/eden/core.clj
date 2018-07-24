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


#_(eval
   x = {:value 12}
   print x.value) ;; 12


;; #_(eval

;;    pair = {:data [nil nil]}
;;    setmeta(pair {:__index (function (this key)
;;                              if key == 0 or key == :first then
;;                                return this.data[0]

;;                              elseif key == 1 or key == :second then
;;                                return this.data[1]

;;                              else
;;                                error("failed to retrieve pair value for key " key)
;;                              end
;;                            end)

;;                  :__assoc (function (this key value)
;;                              if key == 0 or key == :first then
;;                                this.data[0] = value
;;                                return this

;;                              elseif key == 0 or key == :second then
;;                                this.data[1] = value
;;                                return this

;;                              else
;;                                error("failed to assoc to key " key)
;;                              end
;;                            end)})
                                  
;;    pair.new = function(first second)
;;      local p = pair
;;      p.first = first
;;      p.second = second
;;      return p
;;    end

;;    function map(coll fn)
;;      local ncoll = []
;;      for val in coll do
;;        ncoll = conj(ncoll fn(val))
;;      end

;;      return ncoll
;;    end

;;    coll = range(1, 4)
;;    coll = map(coll, inc)


;;    ;; Equivalent
;;    coll ..= conj(2)
;;    coll = coll.conj(ncoll 2)
;;    coll = coll..conj(2)

;;    point = {:x 2 :y 3}

;;    point.x ..= inc()
;;    point.x += 1
;;    point.x = point.x..inc())
