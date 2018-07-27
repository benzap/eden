(ns eden.core-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.core :as eden :include-macros true]
   [eden-test.utils :refer [teval-expression teval are-eq* with-test-instance]
    :include-macros true]))


(deftest basic-arithmetic-addition-subtraction
  (testing "Addition / Subtraction"
    (are-eq*
     (teval-expression 2 + 2)
     
     => 4

     (teval-expression 2 + 2 - 4)
     
     => 0

     (teval-expression 4 - 2 + 2)
     
     => 4)))


(deftest basic-arithmetic-multiplication-division
  (testing "Multiplication / Division"
    (are-eq*
     (teval-expression 2 * 4)
     
     => 8

     (teval-expression 2 / 4.)
     
     => 0.5)))


(deftest basic-arithmetic-equality
  (testing "Equality / Non-equality"
    (are-eq*
     (teval-expression 2 == 2)
     
     => true

     (teval-expression 2 != 2)
     
     => false

     (teval-expression 2 + 2 == 2 * 2)
     
     => true

     (teval-expression 2 - 2 != 2 / 2)
     
     => true)))


(deftest logical-comparators
  (testing "Logical And / Or"
    (are-eq*
     (teval-expression 2 == 2 and "true" or "false")
     
     => "true"

     (teval-expression 2 != 2 and "true" or "false")
     
     => "false")))


(deftest group-expressions
  (testing "Grouping Expressions"
    (are-eq*
     (teval-expression (2 + 2) * 4)
     
     => 16

     (teval-expression (2 * 2 + 4) and "t" or "f")
     
     => "t")))


(deftest declaration-and-assignment
  (testing "Declaring Variable"
    (with-test-instance
      (teval x = 5)
      (is (= (eden/get-var 'x) 5)))))


(deftest reassigning-variable
  (testing "Reassigning Variable"
    (with-test-instance
      (teval x = 5 x = 10)
      (is (= (eden/get-var 'x) 10)))))


(deftest local-variable-1
  (testing "Local Variable in if statement"
    (eden/reset-instance!)
    (eden/eval
     x = 5
     if true then
       println(x)
       local x = 2
       y = x
     end
     println(x))
    
    (is (= (eden/get-var 'x) 5))
    (is (= (eden/get-var 'y) 2))
    (eden/reset-instance!)))


(deftest create-function-1
  (eden/reset-instance!)
  (eden/eval
   local add2 = function(x) return x + 2 end
   local result = add2(2))
  (is (= (eden/get-var 'result) 4))
  (eden/reset-instance!))


(deftest create-function-2
  (eden/reset-instance!)
  (eden/eval
   function add2(x) return x + 2 end
   local result = add2(2))
  (is (= (eden/get-var 'result) 4))
  (eden/reset-instance!))
  
