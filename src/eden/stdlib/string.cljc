(ns eden.stdlib.string
  (:require
   [clojure.string :as str]
   [eden.def :refer [set-variable! set-function!]]))

(def string
  {:blank? str/blank?
   :capitalize str/capitalize
   :ends-with? str/ends-with?
   :escape str/escape
   :includes? str/includes?
   :index-of str/index-of
   :join str/join
   :last-index-of str/last-index-of
   :lower-case str/lower-case
   :re-quote-replacement str/re-quote-replacement
   :replace str/replace
   :replace-first str/replace-first
   :reverse str/reverse
   :split str/split
   :split-lines str/split-lines
   :starts-with? str/starts-with?
   :trim str/trim
   :trim-newline str/trim-newline
   :triml str/triml
   :trimr str/trimr
   :upper-case str/upper-case})


(defn import-stdlib-string
  [eden]
  (set-variable! eden 'string string))
