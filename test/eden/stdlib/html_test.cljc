(ns eden.stdlib.html-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.core :as eden :include-macros true]
   [eden-test.utils :refer [teval-expression teval are-eq* with-test-instance]
    :include-macros true]))


(deftest test-html
  (testing "Main Test"
    (with-test-instance
      (teval
       local x = html.parse("<a href=\"test.html\"></a>")
       local y = html.stringify(x))
      (is (= '([:html {} [:head {}] [:body {} [:a {:href "test.html"}]]])
             (eden/get-var 'x)))
      (is (= "<html><head></head><body><a href=\"test.html\"></a></body></html>"
             (eden/get-var 'y))))))

