(ns eden.def
  (:require
   [eden.std.meta :as meta]
   [eden.state-machine :as state]
   [eden.std.expression :refer [Expression evaluate-expression]]))


(defrecord EdenFFI [func]
  meta/EdenCallable
  (__call [_ args] (apply func args))

  Expression
  (evaluate-expression [this] this))


(defn wrap-function
  [func]
  (->EdenFFI func))


(defn set-function!
  [eden name func]
  (swap! (:*sm eden) state/set-global-var name (wrap-function func))
  eden)

