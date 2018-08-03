(ns eden.std.display)


(defprotocol Display
  (display-node [this]))


(defn display-do-statement [stmts]
  (loop [s (str "(do") stmts stmts]
    (if-not (empty? stmts)
      (recur (str s " " (display-node (first stmts))) (rest stmts))
      (str s ")"))))
