(ns got-records.specs-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [got-records.specs :as nut]
            [clojure.spec.alpha :as s]))

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

;; generate 1000 random records and check against the spec (since we use custom generators)
(defspec generated-data-fulfills-spec
  1000
  (prop/for-all [v (s/gen ::nut/person)]
    (s/valid? ::nut/person v)))

