(ns eden.stdlib.shell
  (:require
   [cuerdas.core :as str]
   [me.raynes.conch :as conch]
   [eden.def :refer [set-var!]]))


;; Native Error: Fails due to missing ProcessBuilder
(defn program-fn [name]
  (fn [& args]
    (apply conch/execute name args)))


(def shell
  {:program program-fn})


(defn import-stdlib-shell
  [eden]
  (set-var! eden 'shell shell))
