(ns eden.stdlib.json-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.core :as eden :include-macros true]
   [eden-test.utils :refer [teval-expression teval are-eq* with-test-instance]
    :include-macros true]))


(deftest test-json
  (testing "Main Test"
    (with-test-instance
      (teval
       local x1 = json.parse("{\"x\" : 123}")
       local x2 = json.parse("{\"x\" : 123}" true)
       local y = json.stringify({:x 123}))
      (is (= {"x" 123} (eden/get-var 'x1)))
      (is (= {:x 123} (eden/get-var 'x2)))
      (is (= "{\"x\":123}" (eden/get-var 'y))))))

