(ns got-records.specs-test
  (:require [clojure.test :refer :all]
            [got-records.specs :as nut]))

(deftest alphanumeric?-predicate-tests
  (testing "allows alphanumeric strings"
    (is (nut/alphanumeric? "abc"))
    (is (nut/alphanumeric? "ABC"))
    (is (nut/alphanumeric? "123"))
    (is (nut/alphanumeric? "ABC123abc456")))
  (testing "fails with our delimeters"
    (is (not (nut/alphanumeric? "ABC DEF")))
    (is (not (nut/alphanumeric? "ABC|DEF")))
    (is (not (nut/alphanumeric? "ABC,DEF"))))
  (testing "asserts with non-string argument"
    (is (thrown? AssertionError (nut/alphanumeric? 10)))
    (is (thrown? AssertionError (nut/alphanumeric? {})))
    (is (thrown? AssertionError (nut/alphanumeric? [])))))