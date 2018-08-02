(ns eden.std.exceptions)


(defn parser-error
  ([msg data]
   (throw (ex-info (str "Eden Parser Error: " msg) {:type ::parser-error :data data})))
  ([msg] (parser-error msg {}))
  ([] (parser-error "No Message")))


(defn parser-error? [ex]
  (let [{:keys [type]} (ex-data ex)]
    (= type ::parser-error)))


(defn runtime-error
  ([msg data]
   (throw (ex-info (str "Eden Runtime Error: " msg) {:type ::runtime-error :data data})))
  ([msg] (runtime-error msg {}))
  ([] (runtime-error "No Message")))


(defn runtime-error? [ex]
  (let [{:keys [type]} (ex-data ex)]
    (= type ::runtime-error)))


(defn not-implemented
  ([msg]
   (throw (ex-info (str "Not Implemented " msg) {:type ::not-implemented})))
  ([] (not-implemented "No Message")))
