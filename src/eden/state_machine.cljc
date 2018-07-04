(ns eden.state-machine)


(defrecord EdenStateMachine
    [contexts
     code-queue
     halt?])


(defn new-state-machine []
  (map->EdenStateMachine
   :contexts '()
   :code-queue '()
   :halt? false))


(defn process-current-context [sm])


(defn step [sm]
  (process-current-context sm))
