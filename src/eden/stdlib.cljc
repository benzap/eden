(ns eden.stdlib
  (:require
   [eden.stdlib.core :refer [import-stdlib-core]]
   [eden.stdlib.collection :refer [import-stdlib-collection]]
   [eden.stdlib.edn :refer [import-stdlib-edn]]
   [eden.stdlib.string :refer [import-stdlib-string]]))


(defn import-stdlib [eden]
  (-> eden
      import-stdlib-core
      import-stdlib-collection
      import-stdlib-edn
      import-stdlib-string))
