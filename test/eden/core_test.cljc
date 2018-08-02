(ns eden.core-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [eden.core :as eden :include-macros true]
   [eden-test.utils :refer [teval-expression teval are-eq* with-test-instance]
    :include-macros true]))
