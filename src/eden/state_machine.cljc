(ns eden.state-machine
  (:require
   [eden.std.environment :as environment :refer [new-environment]]))


(defrecord EdenStateMachine
    [environments])


(defn new-state-machine []
  (map->EdenStateMachine
   {:environments
    [(new-environment) ;; Global
     (new-environment)]})) ;; Local
     

(defn add-environment
  ([sm env] (update-in sm [:environments] conj env))
  ([sm] (add-environment sm (new-environment))))


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
  "Set the global variable, namely the first environment with
  `identifier` to `value`."
  [sm identifier value]
  (let [env (-> sm :environments first (environment/set-var identifier value))]
    (assoc-in sm [:environments 0] env)))


(defn set-local-var
  "Set the local variable, namely the last environment with `identifier`
  to `value`."
  [sm identifier value]
  (let [env (-> sm :environments last (environment/set-var identifier value))]
    (assoc-in sm [:environments (last-env-index sm)] env)))


(defn set-var
  "If a local variable is found with a given `identifier` (environment
  index > 0) then set the `identifier` in the given environment with
  the given `value`. Otherwise, create a global variable (environment
  at index 0) with the given `identifier` to the given `value`."
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
