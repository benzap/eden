(ns eden.stdlib.markdown
  (:require
   [markdown.core :refer [md-to-html-string]]
   [eden.def :refer [set-var!]]))


(def markdown {:stringify md-to-html-string})


(defn import-stdlib-markdown
  [eden]
  (set-var! eden 'markdown markdown))
