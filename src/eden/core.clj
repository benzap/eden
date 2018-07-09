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
  

(evaluate-expression '[2 + 2 * 2])
