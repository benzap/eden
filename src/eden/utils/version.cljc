(ns eden.utils.version)


(defn get-project-version []
  (System/getProperty "eden.version"))


(defn print-project-version []
  (println (get-project-version)))
