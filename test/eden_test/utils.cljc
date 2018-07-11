(ns eden-test.utils
  (:require
   [clojure.test :refer [deftest is testing are]]
   [eden.core :as eden :include-macros true]))


(defmacro teval [& tokens]
  `(eden/eval ~@tokens))


;;(teval x = 5)


(defmacro teval-expression [& tokens]
  `(eden/eval-expression ~@tokens))



;;(teval-expression 2 + 2)


(defmacro are-eq* [& body]
  `(are [x# _sep# y#] (= y# x#)
     ~@body))


(defmacro with-test-instance
  [& body]
  `(eden/with-eden-instance (eden/eden)
     ~@body
     (eden/reset-instance!)
     nil))
