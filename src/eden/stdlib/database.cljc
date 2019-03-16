(ns eden.stdlib.database
  (:require
   [clojure.java.jdbc :as jdbc]
   [eden.def :refer [set-var!]]))


(defn create-sqlite-schema
  "Create an SQLite Schema from the given database path. if no path is
  given, it is assumed to be an in-memory database."
  ([db-path]
   {:classname "org.sqlite.JDBC"
    :subprotocol "sqlite"
    :subname db-path}))


(def database
  {:create-sqlite-schema create-sqlite-schema
   :query jdbc/query
   :execute! jdbc/execute!
   :insert! jdbc/insert!
   :insert-multi! jdbc/insert-multi!
   :update! jdbc/update!
   :delete! jdbc/delete!})


(defn import-stdlib-database
  [eden]
  (-> eden
      (set-var! 'database database)))
