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

(deftest date-conversion-tests
  (testing "dates are converted to expected string format (no leading zeros)"
    (let [lead-zero-month-day #inst "2000-01-01"
          lead-zero-month     #inst "2000-01-20"
          lead-zero-day       #inst "2000-10-01"]
      (is (= (nut/dob->string lead-zero-month-day)
             "1/1/2000"))
      (is (= (nut/dob->string lead-zero-month)
             "1/20/2000"))
      (is (= (nut/dob->string lead-zero-day)
             "10/1/2000")))

    (testing "asserts with non-date argument"
      (is (thrown? AssertionError (nut/dob->string "1/1/2000")))
      (is (thrown? AssertionError (nut/dob->string 1000))))))

(deftest gender-conversion-tests
  (testing "converting gender keyword to string"
    (is (= "female" (nut/gender->string :female)))
    (is (= "male"   (nut/gender->string :male)))))

;; generate 1000 random records and check against the spec (since we use custom generators)
(defspec generated-data-fulfills-spec
  1000
  (prop/for-all [v (s/gen ::nut/person)]
    (s/valid? ::nut/person v)))



