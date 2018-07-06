(ns eden.state-machine
  (:require
   [eden.context.global :refer [new-global-context]]))


(defrecord EdenStateMachine
    [contexts
     code-queue
     halt?])


(defn new-state-machine []
  (map->EdenStateMachine
   :contexts '((new-global-context))
   :code-queue '()
   :halt? false))


(defn process-current-context
  [sm])


(defn step
  [sm]
  (process-current-context sm))
