(ns eden.state-machine.context
  (:require [eden.utils.context :as utils.context]))


(defn current-context
  [sm]
  (-> sm :contexts peek))


(defn evaluation-dispatch-fn
  ""
  [sm]
  ((juxt ::utils.context/name ::utils.context/state) (current-context)))


(defmulti evaluate evaluation-dispatch-fn)
