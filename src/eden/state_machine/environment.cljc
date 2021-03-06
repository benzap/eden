(ns eden.state-machine.environment
  (:require
   [eden.state-machine :as state]
   [eden.std.environment :as std.environment]))


(defmacro with-new-environment
  [*sm & body]
  `(do
     (swap! ~*sm state/add-environment)
     ~@body
     (swap! ~*sm state/remove-environment)))


(defn get-global-environment
  [*sm]
  (-> @*sm :environments first))


(defn get-last-environment
  [*sm]
  (-> @*sm :environments last))


(defn get-closure-environment
  "Retrieves the preceding environments and merges it into an
  environment for use as a closure environment. Note that it does not
  include the global environment."
  [*sm]
  (let [;; Drop the global environment
        envs (rest (-> @*sm :environments))
        values (map :values envs)
        mvalues (reduce merge values)]
    (std.environment/new-environment mvalues)))

