(ns eden.stdlib.edn-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.core :as eden :include-macros true]
   [eden-test.utils :refer [teval-expression teval are-eq* with-test-instance]
    :include-macros true]))


(deftest test-edn
  (testing "Main Test"
    (with-test-instance
      (teval
       local x = edn.parse("{:x 123}")
       local y = edn.stringify(x))
      (is (= {:x 123} (eden/get-var 'x)))
      (is (= "{:x 123}" (eden/get-var 'y))))))

