(ns eden.stdlib.json
  (:require
   [cheshire.core :refer [generate-string parse-string]]
   [eden.def :refer [set-var!]]))


(def json {:parse parse-string
           :stringify generate-string})


(defn import-stdlib-json
  [eden]
  (set-var! eden 'json json))
