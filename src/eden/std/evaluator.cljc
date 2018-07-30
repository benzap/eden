(ns eden.std.evaluator
  (:refer-clojure :exclude [read-string])
  (:require
   [clojure.tools.reader.edn :as edn]
   [eden.std.expression :as std.expression]
   [eden.std.statement :as std.statement]))


(defn evaluate-expression
  [expr]
  (std.expression/evaluate-expression expr))


(defn evaluate
  [stmts]
  (doseq [stmt stmts]
    (std.statement/evaluate-statement stmt)))
    

(defn wrap-eval-string
  [s]
  (str "\n[\n" s "\n]\n"))


(defn read-string [s]
  (let [s (wrap-eval-string s)]
    (edn/read-string s)))


(defn read-file [spath]
  (read-string (slurp spath)))
