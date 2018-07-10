(ns eden.core-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden-test.utils :refer [teval-expression teval are-eq*]]))


(deftest basic-arithmetic
  (testing "Addition / Subtraction"
    (are-eq*
     (teval-expression 2 + 2)
     
     => 4

     (teval-expression 2 + 2 - 4)
     
     => 0

     (teval-expression 4 - 2 + 2)
     
     => 0))

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
