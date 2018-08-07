(ns eden.stdlib.html
  (:require
   [hiccup.core :as hiccup]
   [hickory.core :as hickory]
   [eden.std.exceptions :refer [not-implemented]]
   [eden.def :refer [set-var!]]))


(def html
  {:parse (fn [s] (hickory/as-hiccup (hickory/parse s)))
   :stringify (fn [x] (hiccup/html x))})


(defn import-stdlib-html
  [eden]
  (-> eden
      (set-var! 'html html)))
