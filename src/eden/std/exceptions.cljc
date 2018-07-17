(ns eden.std.exceptions)


(defn not-implemented
  ([msg]
   (throw (Throwable. (str "Not Implemented " msg))))
  ([] (not-implemented "")))
