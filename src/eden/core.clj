(ns eden.core
  (:require
   [eden.state-machine :refer [new-state-machine]]
   [eden.ast :refer [astm]]))


(defn eden []
  (let [*sm (atom (new-state-machine))]
    {:*sm *sm
     :astm (astm *sm)}))


(defn evaluate-expression [tokens]
  (let [eden (eden)]
    (eden.ast/evaluate-expression (:astm eden) tokens)))


(defn parse [tokens]
  (let [eden (eden)]
    (eden.ast/parse (:astm eden) tokens)))


(defn evaluate [tokens]
  (let [eden (eden)]
    (eden.ast/evaluate (:astm eden) tokens)))


;;(evaluate-expression '[ten])
;;(evaluate-expression '[10 != 12 and "Yes" or "No"])


;;(evaluate '[print "Hello World"])

;;(parse '[print "Hello World" 2 + 2])
