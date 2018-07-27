(ns eden.state-machine-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.state-machine :refer [new-state-machine
                               add-environment
                               remove-environment
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


(deftest multiple-environments
  (let [sm (-> (new-state-machine)
               (set-local-var 'x 5)
               (add-environment)
               (set-local-var 'x 2)
               (remove-environment))]
    (is (= 5 (get-var sm 'x)))))
               

