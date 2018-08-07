(ns eden.stdlib.string
  (:require
   [cuerdas.core :as str]
   [eden.std.exceptions :refer [not-implemented]]
   [eden.def :refer [set-var!]]
   #?(:clj (gen-class))))


(def string
  {:<<- str/<<-
   :istr #(not-implemented "'istr' is not implemented (macro with dynamic invokation)")
   :alnum? str/alnum?
   :alpha? str/alpha?
   :blank? str/blank?
   :camel str/camel
   :capital str/capital
   :caseless= str/caseless=
   :chars str/chars
   :clean str/clean
   :collapse-whitespace str/collapse-whitespace
   :css-selector str/css-selector
   :digits? str/digits?
   :empty? str/empty?
   :empty-or-nil? str/empty-or-nil?
   :ends-with? str/ends-with?
   :format str/format
   :human str/human
   :includes? str/includes?
   :join str/join
   :js-selector str/js-selector
   :kebab str/kebab
   :keyword str/keyword
   :letters? str/letters?
   :lines str/lines
   :lower str/lower
   :ltrim str/ltrim
   :numeric? str/numeric?
   :pad str/pad
   :parse-double str/parse-double
   :parse-int str/parse-int
   :parse-number str/parse-number
   :pascal str/pascal
   :phrase str/phrase
   :prune str/prune
   :quote str/quote
   :repeat str/repeat
   :replace str/replace
   :replace-first str/replace-first
   :reverse str/reverse
   :rtrim str/rtrim
   :slice str/slice
   :slug str/slug
   :snake str/snake
   :split str/split
   :starts-with? str/starts-with?
   :strip-newlines str/strip-newlines
   :strip-prefix str/strip-prefix
   :strip-suffix str/strip-suffix
   :strip-tags str/strip-tags
   :surround str/surround
   :title str/title
   :to-bool str/to-bool
   :trim str/trim
   :unlines str/unlines
   :unquote str/unquote
   :unsurround str/unsurround
   :upper str/upper
   :word? str/word?
   :words str/words
   })


(defn import-stdlib-string
  [eden]
  (-> eden
      (set-var! 'string string)))
