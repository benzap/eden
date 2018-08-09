(ns eden.stdlib.filesystem
  (:require
   [me.raynes.fs :as fs]
   [eden.def :refer [set-var!]]))


(def filesystem
  {:absolute fs/absolute
   :absolute? fs/absolute?
   :base-name fs/base-name
   :chdir fs/chdir
   :child-of? fs/child-of?
   :chmod fs/chmod
   :copy fs/copy
   :copy+ fs/copy+
   :copy-dir fs/copy-dir
   :create fs/create
   :cwd (fn [] fs/*cwd*)
   :delete fs/delete
   :delete-dir fs/delete-dir
   :directory? fs/directory?
   :exec fs/exec
   :executable fs/executable?
   :exists? fs/exists?
   :expand-home fs/expand-home
   :extension fs/extension
   :file fs/file
   :file? fs/file
   :find-files fs/find-files
   :find-files* fs/find-files*
   :glob fs/glob
   :hidden? fs/hidden?
   :home fs/home
   :iterate-dir fs/iterate-dir
   :link fs/link
   :link? fs/link?
   :list-dir fs/list-dir
   :mkdir fs/mkdir
   :mkdirs fs/mkdirs
   :mod-time fs/mod-time
   :name fs/name
   :normalized fs/normalized
   :ns-path fs/ns-path
   :parent fs/parent
   :parents fs/parents
   :path-ns fs/path-ns
   :read-sym-link fs/read-sym-link
   :readable? fs/readable?
   :rename fs/rename
   :size fs/size
   :split fs/split
   :split-ext fs/split-ext
   :sym-link fs/sym-link
   :temp-dir fs/temp-dir
   :temp-file fs/temp-file
   :temp-name fs/temp-name
   :tmpdir fs/tmpdir
   :touch fs/touch
   :unix-root fs/unix-root
   :walk fs/walk
   :with-cwd #(fs/with-cwd %1 (%2))
   :with-mutable-cwd #(fs/with-mutable-cwd (%))
   :writeable? fs/writeable?})


(defn import-stdlib-filesystem
  [eden]
  (-> eden
      (set-var! 'filesystem filesystem)))
