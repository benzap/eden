(ns eden.repl
  "Clojure Implementation of basic repl."
  (:refer-clojure :exclude [run!])
  (:require
   [cuerdas.core :as str]
   [eden.core :as eden]))


(def help-message "")


(defn run! []
  (println "Eden Repl")
  (println " 'help' for Help Message,")
  (println " 'exit' to Exit.")
  (flush)
  (loop []
    (print "> ")
    (flush)
    (let [sform (-> (read-line) str/trim)]
      (cond
       (= sform "exit")
       (println "Goodbye!")
       (= sform "help")
       (println help-message)
       :else
       (do
         (println (eden/eval-string sform))
         (flush)
         (recur))))))
