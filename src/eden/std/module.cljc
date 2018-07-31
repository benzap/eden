(ns eden.std.module
  (:require
   [clojure.string :as str])
  (:import java.io.File))


(defn join-path* [^String p1 ^String p2]
  (let [f1 (File. p1)
        f2 (File. f1 p2)]
    (.getPath f2)))


(defn join-path [& p]
  (reduce join-path* p))


(defn is-file? [^String p]
  (let [f (File. p)]
    (.isFile f)))


;;(join-path "../" "test" "test2" "test3")
;;(join-path "test/foo")


(defn module-path->file-path [^String s]
  (apply join-path (str/split s #"/")))


;;(module-path->file-path "bar/foo")


(defn get-environment-paths []
  (if-let [paths (System/getenv "EDEN_MODULE_PATH")]
    (vec (str/split paths #":"))
    []))


(def ^:dynamic *eden-module-path-list*
  (concat
   (get-environment-paths)
   ["."]))


(defn resolve-module-file-path [^String s]
  (loop [paths (reverse *eden-module-path-list*)]
    (if-not (empty? paths)
      (let [path (join-path (first paths) (module-path->file-path s))]
        (if (is-file? (str path ".eden"))
          (str path ".eden")
          (recur (rest paths)))))))


