(ns eden.stdlib.transit
  (:require
   [cognitect.transit :as transit]
   [eden.def :refer [set-var!]])
  (:import [java.io ByteArrayInputStream ByteArrayOutputStream]))


(defn stringify [x]
  (let [out (ByteArrayOutputStream. 4096)
        writer (transit/writer out :json)]
    (transit/write writer x)
    (.toString out)))


(defn parse [s]
  (let [in (ByteArrayInputStream. (.getBytes s))
        reader (transit/reader in :json)]
    (transit/read reader)))


(def transit
  {:stringify stringify
   :parse parse})


(defn import-stdlib-transit
  [eden]
  (-> eden
      (set-var! 'transit transit)))


(comment
 (stringify {:a 123})
 (parse (stringify {:a 123})))
