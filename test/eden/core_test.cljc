(ns eden.core-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.core :as eden]
   [eden-test.utils :refer [teval-expression teval are-eq* with-test-instance]]))


(deftest basic-arithmetic
  (testing "Addition / Subtraction"
    (are-eq*
     (teval-expression 2 + 2)
     
     => 4

     (teval-expression 2 + 2 - 4)
     
     => 0

     (teval-expression 4 - 2 + 2)
     
     => 4))

  (testing "Multiplication / Division"
    (are-eq*
     (teval-expression 2 * 4)
     
     => 8

     (teval-expression 2 / 4.)
     
     => 0.5))

  (testing "Equality / Non-equality"
    (are-eq*
     (teval-expression 2 == 2)
     
     => true

     (teval-expression 2 != 2)
     
     => false

     (teval-expression 2 + 2 == 2 * 2)
     
     => true

     (teval-expression 2 - 2 != 2 / 2)
     
     => true))

  (testing "Logical And / Or"
    (are-eq*
     (teval-expression 2 == 2 and "true" or "false")
     
     => "true"

     (teval-expression 2 != 2 and "true" or "false")
     
     => "false"))

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
      (is (= (eden/get-var 'x) 5))))

  (testing "Reassigning Variable"
    (with-test-instance
      (teval x = 5 x = 10)
      (is (= (eden/get-var 'x) 10))))

  (testing "Local Variable in if statement"
    (with-test-instance
      (teval x = 5
             if true then
               local x = 2
               y = x
             end)
      (is (= (eden/get-var 'x) 5))
      (is (= (eden/get-var 'y) 2)))))
