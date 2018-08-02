(ns eden.stdlib.edn
  (:require
   [clojure.tools.reader.edn :as edn]
   [eden.def :refer [set-var!]]))


(def edn {:parse edn/read-string
          :stringify pr-str})


(defn import-stdlib-edn
  [eden]
  (set-var! eden 'edn edn))

