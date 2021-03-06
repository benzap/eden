(ns eden.std.exceptions
  (:require
   [clojure.pprint :refer [pprint]]))


(def ^:dynamic *file-path* nil)
(def ^:dynamic *verbose* false)

(defn generate-window
  "TODO: write this function, with bigger window."
  [{:keys [tokens index] :as astm}]
  (let []))


(defn generate-parser-data
  [{:keys [tokens index] :as astm}]
  {:file *file-path*
   :position index
   :window
   (str 
    (if (> index 1) "... " "|BEGINNING| ")
    (get tokens (dec index) "") " "
    "'" (get tokens index) "' "
    (get tokens (inc index) "")
    (if (< index (- (count tokens) 3)) " ..." " |END|"))})


(defn parser-error
  ([astm msg extra]
   (throw (ex-info (str "Eden Parser Error: " msg)
                   {:type ::parser-error
                    :msg msg
                    :data (generate-parser-data astm)
                    :extra extra})))
  ([astm msg] (parser-error astm msg {}))
  ([astm] (parser-error astm "No Message")))


(defn parser-error? [ex]
  (let [{:keys [type]} (ex-data ex)]
    (= type ::parser-error)))


(defn runtime-error
  ([msg data]
   (throw (ex-info (str "Eden Runtime Error: " msg) {:type ::runtime-error :data data})))
  ([msg] (runtime-error msg {:file *file-path*}))
  ([] (runtime-error "No Message")))


(defn runtime-error? [ex]
  (let [{:keys [type]} (ex-data ex)]
    (= type ::runtime-error)))


(defn not-implemented
  ([msg]
   (throw (ex-info (str "Not Implemented " msg) {:type ::not-implemented})))
  ([] (not-implemented "No Message")))


(defn default-error-handler [ex]
  (if-not *verbose*
    (cond
      (parser-error? ex)
      (pprint (ex-data ex))

      (runtime-error? ex)
      (pprint (ex-data ex))

      :else (binding [*out* *err*] (println (str ex))))
    (throw ex)))
