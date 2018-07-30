(ns eden.commandline
  "Used for the standalone eden executable."
  (:require
   [clojure.string :as str]
   [clojure.java.io :as io]
   [clojure.tools.cli :refer [parse-opts]]
   [eden.client :refer [form]]
   [eden.core :as eden])
  (:gen-class))


(def help-message
  "eden Language Commandline eval
Usage:
  eden <options>
  eden <filename> [arguments..] [options]
  
Options:
  -h, --help    Show this screen.
  -e            Evaluate Commandline Arguments
Website:
  github.com/benzap/eden
Notes:
  * Commandline Arguments are placed in the variable sys.args")


(def cli-options
 [["-h" "--help"]
  ["-e" nil :id :eval]])


(defn -main
  [& args]
  (let [{:keys [options arguments errors]}
        (parse-opts args cli-options)]
    (cond
     (not (empty? errors))
     (do
       (println "CMDERROR")
       (doseq [errstr errors]
         (println errstr))
       (println)
       (println help-message))

     (:help options)
     (println help-message)

     (:eval options)
     (println (eden/eval-expression-string (str/join " " arguments)))

     (> (count arguments) 0)
     (let [[filename & args] arguments
           sform (slurp filename)]
       (eden/eval sys = sys or {})
       (eden/eval (form sys.args = %clj args))
       (eden/eval-string sform)
       (flush))

     :else
     (println help-message))))
