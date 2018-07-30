(ns eden.stdlib
  (:require
   [eden.stdlib.core :refer [import-stdlib-core]]
   [eden.stdlib.collection :refer [import-stdlib-collection]]
   [eden.stdlib.edn :refer [import-stdlib-edn]]))


(defn import-stdlib [eden]
  (-> eden
      import-stdlib-core
      import-stdlib-collection
      import-stdlib-edn))
