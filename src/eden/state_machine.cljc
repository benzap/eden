(ns eden.state-machine
  (:require
   [eden.std.environment :as environment :refer [new-environment]]))


(defrecord EdenStateMachine
    [environments])


(defn new-state-machine []
  (map->EdenStateMachine
   {:environments [(new-environment)]}))


(defn add-environment
  [sm]
  (update-in sm [:environments] conj (new-environment)))


(defn remove-environment
  [sm]
  (update-in sm [:environments] pop))


(defn get-var
  [sm identifier]
  (let [values (map :values (:environments sm))
        mvalues (reduce merge values)]
    (get mvalues identifier)))


(defn last-env-index
  [sm]
  (-> sm :environments count dec))


(defn set-global-var
  [sm identifier value]
  (let [env (-> sm :environments first (environment/set-var identifier value))]
    (assoc-in sm [:environments 0] env)))


(defn set-local-var
  [sm identifier value]
  (let [env (-> sm :environments last (environment/set-var identifier value))]
    (assoc-in sm [:environments (last-env-index sm)] env)))


(defn set-var
  [sm identifier value]
  (let [values (map :values (:environments sm))
        ivalues (map #(get % identifier ::miss) values)
        ivalues (map-indexed (fn [idx itm] [idx itm]) ivalues)
        ivalues (filter #(not= (second %) ::miss) ivalues)]
    (if-let [ivalue (last ivalues)]
      (let [env (-> sm :environments
                    (nth (first ivalue))
                    (environment/set-var identifier value))]
        (assoc-in sm [:environments (first ivalue)] env))
      (set-global-var sm identifier value))))
