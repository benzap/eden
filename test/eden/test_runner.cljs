(ns eden.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests doo-all-tests]]

   ;; Test Cases ./
   [eden.core-test]))


(doo-tests

 ;; Test Cases ./
 'eden.core-test)

