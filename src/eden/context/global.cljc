(ns eden.context.global
  "The global context."
  (:require
   [eden.state-machine.context :as context]))


(defn new-global-context
  []
  (-> context/default-context
      context/add-variable-scope
      context/set-name ::global))
