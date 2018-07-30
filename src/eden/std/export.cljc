(ns eden.std.export)


;; within the export statement, determines if an export statement
;; should throw the export exception. This is set to true when a
;; required script will be exported.
(def ^:dynamic *enable-export?* false)


(defn throw-export-value
  [value]
  (throw
   (ex-info "Eden Export Statement"
            {:type ::export-value :value value})))


(defn catch-export-value
  [ex]
  (let [{:keys [type value]} (ex-data ex)]
    (if (= type ::export-value)
      value
      (throw ex))))
