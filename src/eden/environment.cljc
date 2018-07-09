(ns eden.environment)


(defrecord EdenEnvironment [values])


(defn new-environment []
  (map->EdenEnvironment {:values {}}))
  

(defn get-var [env identifier]
  (-> env :values identifier))


(defn set-var [env identifier value]
  (update-in env [:values] assoc identifier value))
