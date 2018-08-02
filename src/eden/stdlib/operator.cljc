(ns eden.stdlib.operator
  (:require
   [eden.def :refer [set-var!]]))


(def operator
  {:add +
   :sub -
   :mult *
   :div /
   :not not
   :and #(and %1 %2)
   :or #(or %1 %2)})


(defn import-stdlib-operator [eden]
  (-> eden
      (set-var! 'operator operator)))
