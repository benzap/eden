(ns eden.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests doo-all-tests]]

   ;; Test Cases ./
   [eden.core-test]
   [eden.state-machine-test]

   ;; Test Cases ./std
   [eden.std.environment-test]))


(doo-tests

 ;; Test Cases ./
 'eden.core-test
 'eden.state-machine-test

 ;; Test Cases ./std
 'eden.std.environment-test)
