(ns eden.std.exceptions)


(defn parser-error
  ([msg data]
   (throw (ex-info (str "Eden Parser Error: " msg) {:type ::parser-error :data data})))
  ([msg] (parser-error msg {}))
  ([] (parser-error "")))


(defn not-implemented
  ([msg]
   (throw (ex-info (str "Not Implemented " msg) {:type ::not-implemented})))
  ([] (not-implemented "")))
