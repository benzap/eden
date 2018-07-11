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


(defn last-env-index
  [sm]
  (-> sm :environments count dec))


(defn set-global-var
  [sm identifier value]
  (let [env (-> sm :environments last (environment/set-var identifier value))]
    (assoc-in sm [:environments 0] env)))


(defn set-local-var
  [sm identifier value]
  (let [env (-> sm :environments last (environment/set-var identifier value))]
    (assoc-in sm [:environments (last-env-index sm)] env)))


(defn set-var
  [sm identifier])
