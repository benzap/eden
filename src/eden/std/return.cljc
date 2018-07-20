(ns eden.std.return
  (:import clojure.lang.ExceptionInfo))


(defn throw-return-value
  [value]
  (throw
   (ex-info "Eden Return Statement"
            {:type ::return-value :value value})))


(defn catch-return-value
  [ex]
  (let [{:keys [type value]} (ex-data ex)]
    (if (= type ::return-value)
      value
      (throw ex))))
