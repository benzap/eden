(ns eden.stdlib.html-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.core :as eden :include-macros true]
   [eden-test.utils :refer [teval-expression teval are-eq* with-test-instance]
    :include-macros true]))


(deftest test-html
  (testing "Main Test"
    (with-test-instance
      (teval
       local x = html.parse("<a href=\"test.html\"></a>")
       local y = html.stringify(x))
      (is (= {:x 123} (eden/get-var 'x)))
      (is (= "{:x 123}" (eden/get-var 'y))))))

