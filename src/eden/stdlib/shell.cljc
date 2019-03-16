(ns eden.stdlib.shell
  (:require
   [cuerdas.core :as str]
   [me.raynes.conch :as conch]
   [eden.def :refer [set-var!]]))


(defn program-fn [name]
  (fn [& args]
    (apply conch/execute name args)))


(def shell
  {:program program-fn})


(defn import-stdlib-shell
  [eden]
  (-> eden
      (set-var! 'shell shell)))
