(ns eden.stdlib.io
  (:require
   [clojure.java.io :as io]
   [eden.def :refer [set-var!]]))


(def io
  {:as-file io/as-file
   :as-relative-path io/as-relative-path
   :as-url io/as-url
   :copy io/copy
   :delete-file io/delete-file
   :file io/file
   :input-stream io/input-stream
   :make-input-stream io/make-input-stream
   :make-output-stream io/make-output-stream
   :make-parents io/make-parents
   :make-reader io/make-reader
   :make-writer io/make-writer
   :output-stream io/output-stream
   :reader io/reader
   :resource io/resource
   :writer io/writer})


(defn import-stdlib-io
  [eden]
  (-> eden
      (set-var! 'io io)))
