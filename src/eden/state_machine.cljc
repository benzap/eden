(ns eden.state-machine
  (:require
   [eden.environment :as environment :refer [new-environment]]))


(defrecord EdenStateMachine
    [environments])


(defn new-state-machine []
  (map->EdenStateMachine
   {:environments [(new-environment)]}))


(defn get-var
  [sm identifier]
  (-> sm :environments first (environment/get-var identifier)))


(defn set-var
  [sm identifier value]
  (let [env (-> sm :environments first (environment/set-var identifier value))]
    (assoc-in sm [:environments 0] env)))


(defn set-global-var
  [sm identifier value])


(defn set-local-var
  [sm identifier value])


(assoc-in [1 2 3 4] [2] 4)


(-> (new-state-machine)
    (set-var 'test :test)
    (get-var 'test))
