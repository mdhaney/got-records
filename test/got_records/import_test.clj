(ns got-records.import-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [got-records.specs :as spec]
            [got-records.import :as nut]))

(deftest split-line-tests
  (testing "splitting with each delimeter"
    (is (= ["John" "Smith"] (nut/split-line "John|Smith")))
    (is (= ["John" "Smith"] (nut/split-line "John Smith")))
    (is (= ["John" "Smith"] (nut/split-line "John,Smith"))))

  (testing "splitting with an invalid delimiter"
    (is (nil? (nut/split-line "John&Smith"))))

  (testing "asserts with non-string argument"
    (is (thrown? AssertionError (nut/split-line 10)))
    (is (thrown? AssertionError (nut/split-line ["string"])))))

(deftest line->record-tests
  (testing "success path"
    (is (= {:first "John" :last "Smith"} (nut/line->record [:first :last] "John|Smith")))
    (is (= {:first "John" :last "Smith"} (nut/line->record [:first :last] "John Smith")))
    (is (= {:first "John" :last "Smith"} (nut/line->record [:first :last] "John,Smith"))))

  (testing "invalid delimiter"
    (is (nil? (nut/line->record [:first :last] "John&Smith"))))

  (testing "asserts with invalid arguments"
    (is (thrown? AssertionError (nut/line->record [:first :last] 10)))
    (is (thrown? AssertionError (nut/line->record [:first :last] ["string"])))
    (is (thrown? AssertionError (nut/line->record :first "John|Smith")))
    (is (thrown? AssertionError (nut/line->record ["first" "last"] "John|Smith")))))

(deftest line->person-tests
  (testing "success path"
    (is (s/valid? ::spec/person (nut/line->person "John|Smith|male|Blue|1/1/2000")))
    (is (s/valid? ::spec/person (nut/line->person "John Smith male Blue 1/1/2000")))
    (is (s/valid? ::spec/person (nut/line->person "John,Smith,male,Blue,1/1/2000"))))

  (testing "bad inputs return nil"
    (is (= ::s/invalid (nut/line->person "")))
    (is (= ::s/invalid (nut/line->person "John|Smith|male|Blue")))
    (is (= ::s/invalid (nut/line->person "John|Smith|guy|Blue|1/1/2000")))
    (is (= ::s/invalid (nut/line->person "John|Smith|male|Blue|yesterday")))))