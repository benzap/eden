(ns eden.std.meta)


(defprotocol EdenCallable
  (__call [this args]))


(defn eden-callable? [obj]
  (satisfies? EdenCallable obj))


(defprotocol EdenIndexable
  (__index [this index]))


;; Similar to lua's __newindex
(defprotocol EdenAssociative
  (__assoc [this index value]))


(defprotocol EdenToString
  (__str [this]))


(defprotocol EdenCountable
  (__count [this]))


(defprotocol EdenIterable
  (__iter [this]))


(defprotocol EdenUnaryMinus
  (__unm [this]))


(defprotocol EdenUnaryNot
  (__not [this]))


(defprotocol EdenAddition
  (__add [this obj]))


(defprotocol EdenSubtraction
  (__sub [this obj]))


(defprotocol EdenMultiplication
  (__mul [this obj]))


(defprotocol EdenDivision
  (__div [this obj]))


(defprotocol EdenConcatenation
  (__concat [this obj]))


