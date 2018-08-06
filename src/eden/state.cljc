(ns eden.state
  (:require
   [eden.state-machine :refer [new-state-machine]]
   [eden.std.ast :refer [astm]]
   [eden.stdlib :refer [import-stdlib]]
   [eden.std.exceptions :as exceptions]))


(defn new-eden-instance []
  (let [*sm (atom (new-state-machine))]
    {:*sm *sm
     :astm (astm *sm)
     :error-handler exceptions/default-error-handler}))


(defn set-error-handler [eden f]
  (assoc eden :error-handler f))


(defn eden []
  (-> (new-eden-instance)
      import-stdlib))


(def ^:dynamic *default-eden-instance* (eden))


(defn reset-instance! []
  (reset! (:*sm *default-eden-instance*) (new-state-machine))
  (import-stdlib *default-eden-instance*)
  nil)
