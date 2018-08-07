(ns eden.stdlib.system-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.core :as eden :include-macros true]
   [eden-test.utils :refer [teval-expression teval are-eq* with-test-instance]
    :include-macros true]))


(deftest test-globals
  (testing "Main Test"
    (with-test-instance
      (teval x = 5
             y = system.get-globals()
             z = y[symbol("x")])
      (is (= 5 (eden/get-var 'z))))

    (with-test-instance
      (teval x = 5
             system.set-global(symbol("x") 6)
             y = system.get-globals()[symbol("x")])
      (is (= 6 (eden/get-var 'y))))))
