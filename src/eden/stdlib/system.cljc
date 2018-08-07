(ns eden.stdlib.system
  (:require
   [clojure.pprint :refer [pprint]]
   [eden.def :refer [set-var!]]))


(defn get-globals
  [{:keys [*sm] :as eden}]
  (-> @*sm
      :environments
      first
      :values))


(defn set-global [eden identifier value]
  (set-var! eden identifier value))


(defn system [eden]
  {:env #(System/getenv %)
   :exit #(System/exit %)
   :get-globals #(get-globals eden)
   :set-global #(set-global eden %1 %2)})


(defn import-stdlib-system
  [eden]
  (-> eden
      (set-var! 'system (system eden))))
