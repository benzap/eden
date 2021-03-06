(ns eden.std.environment-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.std.environment :refer [new-environment get-var set-var]]
   [eden-test.utils :refer [are-eq*] :include-macros true]))


(deftest main-test
  (testing "Setting and Getting"
    (let [env (-> (new-environment)
                  (set-var 'test 123))]
      (is (= (get-var env 'test) 123))
      (is (= (get-var env 'test2) nil)))))
