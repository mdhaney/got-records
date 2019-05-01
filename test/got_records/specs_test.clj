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
      (is (thrown? AssertionError (nut/dob->string 1000)))))

  (testing "dates are converted from expected string format"
    (let [correct #inst "2000-01-01"]
      (is (= correct (nut/string->dob "1/1/2000")))
      (is (= correct (nut/string->dob "01/1/2000")))
      (is (= correct (nut/string->dob "1/01/2000")))
      (is (= correct (nut/string->dob "01/01/2000"))))

    (testing "asserts with non-string argument"
      (is (thrown? AssertionError (nut/string->dob [])))
      (is (thrown? AssertionError (nut/string->dob 1000))))

    (testing "returns nil for unparsable dates"
      (is (nil? (nut/string->dob "1/1")))
      (is (nil? (nut/string->dob "1/1/00")))
      (is (nil? (nut/string->dob "")))
      (is (nil? (nut/string->dob "male")))
      (is (nil? (nut/string->dob "blue"))))))

(deftest gender-conversion-tests
  (testing "converting gender keyword to string"
    (is (= "female" (nut/gender->string :female)))
    (is (= "male"   (nut/gender->string :male)))

    (testing "asserts with non-keyword arguments"
      (is (thrown? AssertionError (nut/gender->string "female")))
      (is (thrown? AssertionError (nut/gender->string true)))
      (is (thrown? AssertionError (nut/gender->string 10)))))

  (testing "converting gender string to keyword"
    (is (= :male (nut/string->gender "male")))
    (is (= :male (nut/string->gender "Male")))
    (is (= :male (nut/string->gender "M")))
    (is (= :male (nut/string->gender "m")))
    (is (= :female (nut/string->gender "female")))
    (is (= :female (nut/string->gender "Female")))
    (is (= :female (nut/string->gender "F")))
    (is (= :female (nut/string->gender "f")))

    (testing "asserts with non-string argument"
      (is (thrown? AssertionError (nut/string->gender true)))
      (is (thrown? AssertionError (nut/string->gender :male))))

    (testing "returns nil for bad string inputs"
      (is (nil? (nut/string->gender "dude")))
      (is (nil? (nut/string->gender "guy")))
      (is (nil? (nut/string->gender "chick")))
      (is (nil? (nut/string->gender "babe"))))))

;; generate 1000 random records and check against the spec (since we use custom generators)
(defspec generated-data-fulfills-spec
  1000
  (prop/for-all [v (s/gen ::nut/person)]
    (s/valid? ::nut/person v)))



