(ns eden.std.meta)


(defprotocol EdenCallable
  (__call [this args]))
