(ns eden.stdlib
  (:require
   [eden.stdlib.core :refer [import-stdlib-core]]
   [eden.stdlib.html :refer [import-stdlib-html]]
   [eden.stdlib.collection :refer [import-stdlib-collection]]
   [eden.stdlib.edn :refer [import-stdlib-edn]]
   [eden.stdlib.json :refer [import-stdlib-json]]
   [eden.stdlib.string :refer [import-stdlib-string]]
   [eden.stdlib.operator :refer [import-stdlib-operator]]
   [eden.stdlib.specter :refer [import-stdlib-specter]]
   [eden.stdlib.system :refer [import-stdlib-system]]
   [eden.stdlib.shell :refer [import-stdlib-shell]]))


(defn import-stdlib [eden]
  (-> eden
      import-stdlib-core
      import-stdlib-html
      import-stdlib-collection
      import-stdlib-edn
      import-stdlib-json
      import-stdlib-string
      import-stdlib-operator
      import-stdlib-specter
      import-stdlib-system
      import-stdlib-shell))
