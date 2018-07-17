(ns eden.std.meta)


(defprotocol EdenCallable
  (__call [this args]))


(defn eden-callable? [obj]
  (satisfies? EdenCallable obj))


(extend-protocol EdenCallable
  String
  (__call [this args]
    (prn this args)))
