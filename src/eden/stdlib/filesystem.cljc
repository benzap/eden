(ns eden.stdlib.filesystem
  (:require
   [me.raynes.fs :as fs]
   [eden.def :refer [set-var!]]))


;; Native Compilation will evaluate and set *cwd* to the native-image
;; build server path, which is less than ideal for the uses of this
;; library. So each function has been wrapped with `wrap-with-cwd` to
;; ensure a new default cwd is set with respect to the actual current
;; working directory.


(defn get-cwd []
  (System/getProperty "user.dir"))


(defn wrap-with-cwd [f]
  (fn [& args]
    (fs/with-cwd (get-cwd)
      (apply f args))))


(def filesystem
  {:absolute (wrap-with-cwd fs/absolute)
   :absolute? (wrap-with-cwd fs/absolute?)
   :base-name (wrap-with-cwd fs/base-name)
   ;;:chdir fs/chdir
   :child-of? (wrap-with-cwd fs/child-of?)
   :chmod (wrap-with-cwd fs/chmod)
   :copy (wrap-with-cwd fs/copy)
   :copy+ (wrap-with-cwd fs/copy+)
   :copy-dir (wrap-with-cwd fs/copy-dir)
   :create (wrap-with-cwd fs/create)
   :cwd (wrap-with-cwd (fn [] fs/*cwd*))
   :delete (wrap-with-cwd fs/delete)
   :delete-dir (wrap-with-cwd fs/delete-dir)
   :directory? (wrap-with-cwd fs/directory?)
   :exec (wrap-with-cwd fs/exec)
   :executable (wrap-with-cwd fs/executable?)
   :exists? (wrap-with-cwd fs/exists?)
   :expand-home (wrap-with-cwd fs/expand-home)
   :extension (wrap-with-cwd fs/extension)
   :file (wrap-with-cwd fs/file)
   :file? (wrap-with-cwd fs/file?)
   :find-files (wrap-with-cwd fs/find-files)
   :find-files* (wrap-with-cwd fs/find-files*)
   :glob (wrap-with-cwd fs/glob)
   :hidden? (wrap-with-cwd fs/hidden?)
   :home (wrap-with-cwd fs/home)
   :iterate-dir (wrap-with-cwd fs/iterate-dir)
   :link (wrap-with-cwd fs/link)
   :link? (wrap-with-cwd fs/link?)
   :list-dir (wrap-with-cwd fs/list-dir)
   :mkdir (wrap-with-cwd fs/mkdir)
   :mkdirs (wrap-with-cwd fs/mkdirs)
   :mod-time (wrap-with-cwd fs/mod-time)
   :name (wrap-with-cwd fs/name)
   :normalized (wrap-with-cwd fs/normalized)
   :ns-path (wrap-with-cwd fs/ns-path)
   :parent (wrap-with-cwd fs/parent)
   :parents (wrap-with-cwd fs/parents)
   :path-ns (wrap-with-cwd fs/path-ns)
   :read-sym-link (wrap-with-cwd fs/read-sym-link)
   :readable? (wrap-with-cwd fs/readable?)
   :rename (wrap-with-cwd fs/rename)
   :size (wrap-with-cwd fs/size)
   :split (wrap-with-cwd fs/split)
   :split-ext (wrap-with-cwd fs/split-ext)
   :sym-link (wrap-with-cwd fs/sym-link)
   :temp-dir (wrap-with-cwd fs/temp-dir)
   :temp-file (wrap-with-cwd fs/temp-file)
   :temp-name (wrap-with-cwd fs/temp-name)
   :tmpdir (wrap-with-cwd fs/tmpdir)
   :touch (wrap-with-cwd fs/touch)
   :unix-root (wrap-with-cwd fs/unix-root)
   :walk (wrap-with-cwd fs/walk)
   ;;:with-cwd #(fs/with-cwd %1 (%2))
   ;;:with-mutable-cwd #(fs/with-mutable-cwd (%))
   :writeable? (wrap-with-cwd fs/writeable?)})


(defn import-stdlib-filesystem
  [eden]
  (-> eden
      (set-var! 'filesystem filesystem)))
