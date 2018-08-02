(ns eden.stdlib
  (:require
   [eden.stdlib.core :refer [import-stdlib-core]]
   [eden.stdlib.collection :refer [import-stdlib-collection]]
   [eden.stdlib.edn :refer [import-stdlib-edn]]
   [eden.stdlib.string :refer [import-stdlib-string]]
   [eden.stdlib.operator :refer [import-stdlib-operator]]))


(defn import-stdlib [eden]
  (-> eden
      import-stdlib-core
      import-stdlib-collection
      import-stdlib-edn
      import-stdlib-string
      import-stdlib-operator))
