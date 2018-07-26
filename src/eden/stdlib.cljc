(ns eden.stdlib
  (:require
   [eden.stdlib.core :refer [import-stdlib-core]]
   [eden.stdlib.collection :refer [import-stdlib-collection]]))


(defn import-stdlib [eden]
  (import-stdlib-core eden)
  (import-stdlib-collection eden))
