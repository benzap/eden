(ns eden.stdlib.system
  (:require
   [eden.def :refer [set-var!]]
   [eden.state-machine.environment :as environment]))


;; TODO: add get-globals command


(def system
  {:env #(System/getenv %)
   :exit #(System/exit %)
   :get-globals (fn [] {})})


(defn import-stdlib-system
  [eden]
  (-> eden
      (set-var! 'system system)))
