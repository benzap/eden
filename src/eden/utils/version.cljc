(ns eden.utils.version
  (:import java.lang.System))


(defn get-project-version []
  (java.lang.System/getProperty "eden.version"))


(defn print-project-version []
  (println (get-project-version)))
