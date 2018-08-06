(ns eden.std-test
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
     
     => "false"))

  (testing "Logical Not"
    (with-test-instance
      (teval
       x = 5
       y = not (x == 5) and :yes or :no)
      (is (= :no (eden/get-var 'y))))

    (with-test-instance
      (teval
       x = 5
       y = not (x != 5 and true or false))
      (is (= true (eden/get-var 'y))))
    
    (with-test-instance
      (teval
       x = 5
       y = not (x != 5))
      (is (= true (eden/get-var 'y))))

    (with-test-instance
      (teval
       x = 5
       y = not (not (x != 5)))
      (is (= false (eden/get-var 'y))))))


(deftest group-expressions
  (testing "Grouping Expressions"
    (are-eq*
     (teval-expression (2 + 2) * 4)
     
     => 16

     (teval-expression (2 * 2 + 4) and "t" or "f")
     
     => "t"

     (teval-expression (2 + 3) * (2 + 4))
     
     => 30)))


(deftest negation
  (testing "Main Test"
    (are-eq*
     (teval-expression - 2 + 2)
     
     => 0)))


(deftest declaration-and-assignment
  (testing "Declaring Variable"
    (with-test-instance
      (teval x = 5)
      (is (= (eden/get-var 'x) 5)))))


(deftest reassigning-variable
  (testing "Reassigning Variable"
    (with-test-instance
      (teval x = 5 x = 10)
      (is (= (eden/get-var 'x) 10)))

    (with-test-instance
      (teval local x = 5 x = 10)
      (is (= (eden/get-var 'x) 10)))

    (with-test-instance
      (teval local x = 5 local x = 10)
      (is (= (eden/get-var 'x) 10)))

    (with-test-instance
      (teval x = 5 local x = 10)
      (is (= (eden/get-var 'x) 10)))))


(deftest if-conditional
  (testing "Main Test"
    (with-test-instance
      (teval
       x = 5
       local chk? = nil
       if x < 2 and x > 5 then
         chk? = :first
       else
         chk? = :second
       end)
      (is (= :second (eden/get-var 'chk?))))

    (with-test-instance
      (teval
       x = -1
       local chk? = nil
       if x < 2 or x > 5 then
         chk? = :first
       else
         chk? = :second
       end)
      (is (= :first (eden/get-var 'chk?))))

    (with-test-instance
      (teval
       chk? = false
       local age = 24
       if age < 18 then
         chk? = 1
       elseif age >= 18 and age < 50 then
         chk? = 2
       else
         chk? = 3
       end)
      (is (= 2 (eden/get-var 'chk?))))

    (with-test-instance
      (teval
       function check-age(age)
         if age < 18 then
           return 1
         elseif age >= 18 and age < 50 then
           return 2
         else
           return 3
         end
       end

       chk1 = check-age(12)
       chk2 = check-age(19)
       chk3 = check-age(50))
      (is (= 1 (eden/get-var 'chk1)))
      (is (= 2 (eden/get-var 'chk2)))
      (is (= 3 (eden/get-var 'chk3))))))


(deftest for-each-loop
  (testing "Main Test"
    (with-test-instance
      (teval
       local x = [1 2 3]
       local y = []
       for i in x do
         y = conj(y i * i)
       end)
      (is (= [1 4 9] (eden/get-var 'y))))))


(deftest for-loop
  (testing "Main Test"
    (with-test-instance
      (teval
       local x = []
       for i = 1 5 1 do
         x = conj(x i)
       end)
      
      (is (= [1 2 3 4 5] (eden/get-var 'x))))

    (with-test-instance
      (teval
       local x = []
       for i = 0 4 2 do
         x = conj(x i)
       end)
      
      (is (= [0 2 4] (eden/get-var 'x))))))


(deftest while-conditional
  (testing "Main Test"
    (with-test-instance
      (teval
       local i = 0
       while i < 5 do
         i = i + 1
       end)
      (is (= 5 (eden/get-var 'i))))

    (with-test-instance
      (teval
       local i = 0
       while not (i > 5) do
         i = i + 1
       end)
      (is (= 6 (eden/get-var 'i))))))


(deftest until-conditional
  (testing "Main Test"
    (with-test-instance
      (teval
       local i = 0
       repeat
         i = i + 1
       until i >= 5)
      (is (= 5 (eden/get-var 'i))))))


(deftest local-variable-1
  (testing "Local Variable in if statement"
    (with-test-instance
      (teval
       x = 5
       if true then
         local x = 2
         y = x
       end)
    
      (is (= (eden/get-var 'x) 5))
      (is (= (eden/get-var 'y) 2)))))


(deftest create-function
  (with-test-instance
    (teval
     local add2 = function(x) return x + 2 end
     local result = add2(2))

    (is (= (eden/get-var 'result) 4)))

  (with-test-instance
    (teval
     local x = {}
     x.test1 = function() return 5 end
     x.test2 = function() return 3 end
     
     y = x.test1() + x.test2())
    (is (= 8 (eden/get-var 'y))))

  ;; closure
  (with-test-instance
    (teval
     function make-counter()
       local count = 0
       return function()
         count = count + 1
         return count
       end
     end
     
     local counter = make-counter()
     counter()
     y = counter())
    (is (= 2 (eden/get-var 'y))))

  ;; local function
  (with-test-instance
    (teval
     local function add(x y)
       return x + y
     end
     
     local add = add(2 2))
    (is (= 4 (eden/get-var 'add))))

  (with-test-instance
    (teval
     function add2(x) return x + 2 end
     local result = add2(2))

    (is (= (eden/get-var 'result) 4))))


(deftest indexed-association
  (testing "Main Test"
    (with-test-instance
      (teval
       local x = {}
       x.test = :test
       x.a.b = 123)
      (is (= {:test :test :a {:b 123}} (eden/get-var 'x))))

    (with-test-instance
      (teval
       local x = [:test :test :test]
       local y = [1 2 3]
       x[0] = y[0]
       x[1] = y[1]
       x[2] = y[2])
      (is (= [1 2 3] (eden/get-var 'x))))

    (with-test-instance
      (teval
       local x = [{:a [{} {:b 123}]}]
       local y = x[0].a[1].b)
      (is (= 123 (eden/get-var 'y))))

    (with-test-instance
      (teval
       local x = [{:a [{} {:b 123}]}]
       x.[0].a[0].c = 124
       local y = x[0].a[0].c)
      (is (= 124 (eden/get-var 'y))))))

(deftest primary-expressions
  (testing "Main Test"
    (with-test-instance
      (teval
       local xmap = true
       local ymap = not xmap)
      (is (= true (eden/get-var 'xmap)))
      (is (= false (eden/get-var 'ymap))))

    (with-test-instance
      (teval
       local x = "some-string"
       local y = string.upper-case(x))
      (is (= "some-string" (eden/get-var 'x)))
      (is (= "SOME-STRING" (eden/get-var 'y))))))
