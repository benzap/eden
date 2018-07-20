(ns eden.std.environment)


(defrecord EdenEnvironment [values])


(defn new-environment
  ([values] (map->EdenEnvironment {:values values}))
  ([] (new-environment {})))
  

(defn get-var [env identifier]
  (-> env :values identifier))


(defn set-var [env identifier value]
  (update-in env [:values] assoc identifier value))
