(ns eden.state-machine.environment
  (:require
   [eden.state-machine :as state]))


(defmacro with-new-environment
  [*sm & body]
  `(do
     (swap! ~*sm state/add-environment)
     ~@body
     (swap! ~*sm state/remove-environment)))
