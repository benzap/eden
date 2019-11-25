(ns eden.stdlib.promise
  (:require
   [eden.def :refer [set-var!]]))


(def promise-ns
  {:new promise
   :deliver deliver
   :realized? realized?})


(defn import-stdlib-promise
  [eden]
  (-> eden
      (set-var! 'promise promise-ns)))
