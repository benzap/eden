(ns eden.commandline
  "Used for the standalone eden executable."
  (:require
   [clojure.pprint :refer [pprint]]
   [clojure.string :as str]
   [clojure.java.io :as io]
   [clojure.tools.cli :refer [parse-opts]]
   [eden.std.module :as module]
   [eden.std.exceptions :as exceptions :refer [*file-path* *verbose*]]
   [eden.client :refer [form]]
   [eden.state :refer [*default-eden-instance*]]
   [eden.core :as eden]
   [eden.repl])
  (:gen-class))


(def help-message
  "eden Language Commandline eval
Usage:
  eden <options>
  eden <filename> [arguments..] [options]
  
Options:
  -h, --help        Show this screen.
  -m, --modulepath  Add Eden Module Paths separated by
                    ':' in Linux or ';' in Windows
  -e                Evaluate Commandline Arguments as an Eden Expression
  -i, --stdin       Read from *in* (system.in)
Website:
  github.com/benzap/eden
Notes:
  * Commandline Arguments are placed in the variable system.argv")


(def cli-options
 [["-h" "--help"]
  ["-m" "--modulepath PATHS" "Module Paths"
   :id :modpath
   :parse-fn #(module/split-module-paths %)]
  ["-v" "--verbose" "Verbose Error Messages"
   :id :verbosity
   :default false]
  ["-e" nil :id :eval]
  ["-i" "--stdin" "Read from *in* (system.in)"
   :id :stdin
   :default false]])


(defn cmdline-module-paths [options]
  (let [modpath module/*eden-module-path-list*]
    (if (:modpath options)
      (concat modpath (:modpath options))
      modpath)))


(defn -main
  [& args]
  (let [{:keys [options arguments errors]}
        (parse-opts args cli-options)]
    
    (when (:stdin options)
      (eden/eval-fn (form system.in = %clj (slurp *in*))))

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
     (binding [module/*eden-module-path-list* (cmdline-module-paths options)
               *verbose* (:verbosity options)]
       (when *verbose*
         (println "Module Paths:" module/*eden-module-path-list*))
       (println (eden/eval-expression-string (str/join " " arguments))))

     (> (count arguments) 0)
     (let [[filename & args] arguments
           sform (slurp filename)]
       (binding [module/*eden-module-path-list* (cmdline-module-paths options)
                 *file-path* filename
                 *verbose* (:verbosity options)]
         (when *verbose*
           (println "Module Paths:" module/*eden-module-path-list*))
         (eden/eval system = system or {})
         (eden/eval-fn (form system.argv = %clj (vec args)))
         (eden/eval-string sform))
       (flush))

     :else
     (eden.repl/run!))
    (System/exit 0)))
