(ns eden.stdlib.transit
  (:require
   [cognitect.transit :as transit]
   [eden.def :refer [set-var!]]))


(def transit
  {:write transit/write
   :read transit/read})


(defn import-stdlib-transit
  [eden]
  (-> eden
      (set-var! 'transit transit)))
