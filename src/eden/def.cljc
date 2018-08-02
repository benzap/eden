(ns eden.def
  (:require
   [eden.std.meta :as meta]
   [eden.state-machine :as state]
   [eden.std.expression :refer [Expression evaluate-expression]]))


(defn set-var!
  [eden name value]
  (swap! (:*sm eden) state/set-global-var name value)
  eden)
