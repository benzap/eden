(ns eden.stdlib.edn
  (:require
   [clojure.tools.reader.edn :as edn]
   [eden.def :refer [set-variable! set-function!]]))


(def edn {:parse edn/read-string
          :stringify pr})


(defn import-stdlib-edn
  [eden]
  (set-variable! eden 'edn edn))

