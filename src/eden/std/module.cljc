(ns eden.std.module
  (:require
   [me.raynes.fs :as fs]
   [clojure.string :as str])
  (:import java.io.File))


(defn join-path* [^String p1 ^String p2]
  (let [f1 (File. p1)
        f2 (File. f1 p2)]
    (.getPath f2)))


(defn join-path [& p]
  (reduce join-path* p))


;;(join-path "~/" ".bin")


(defn is-file? [^String p]
  (let [f (File. p)]
    (.isFile f)))


(def os-name (System/getProperty "os.name"))
(def windows-machine?
  (boolean (re-matches #"win|windows.*" (str/lower-case os-name))))


(def module-path-separator (if windows-machine? #";" #":"))


;;(join-path "../" "test" "test2" "test3")
;;(join-path "test/foo")
(defn home []
  (System/getProperty "user.home"))

(defn module-path->file-path [^String s]
  (apply join-path (str/split s #"/")))


;;(module-path->file-path "bar/foo")


(defn split-module-paths [s]
  (vec (str/split s module-path-separator)))


(defn get-environment-paths []
  (if-let [paths (System/getenv "EDEN_MODULE_PATH")]
    (split-module-paths paths)
    []))


;;(get-environment-paths)


(def ^:dynamic *eden-module-path-list*
  (concat
   (if-not windows-machine?
     ["/usr/share/eden/libs"]
     [])
   (get-environment-paths)
   [(join-path (home) "/.eden/libs")]))


(fs/absolute (fs/expand-home "/.eden/libs"))


(def cwd ".")
(defn resolve-module-file-path [^String s]
  (loop [paths (reverse (conj *eden-module-path-list* cwd))]
    (if-not (empty? paths)
      (let [path (join-path (first paths) (module-path->file-path s))]
        (if (is-file? (str path ".eden"))
          (str path ".eden")
          (recur (rest paths)))))))




