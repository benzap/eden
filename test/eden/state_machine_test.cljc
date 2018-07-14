(ns eden.state-machine-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.state-machine :refer [new-state-machine
                               get-var
                               set-var
                               set-global-var
                               set-local-var]]
   [eden-test.utils :refer [are-eq*]]))


(deftest main-test
  (testing "Setting and Getting global environment"
    (let [sm (-> (new-state-machine)
                 (set-global-var 'test 123))]
      (is (= 123 (get-var sm 'test))))))
