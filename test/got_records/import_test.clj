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

(deftest import-seq-tests
  (testing "importing a batch of records"
    (are [expected-valid expected-skipped input]
      (let [result (nut/import-seq input)
            actual-skipped (:skipped result)
            actual-valid (count (:data result))]
        (is (= expected-valid actual-valid))
        (is (= expected-skipped actual-skipped)))

      3 0 ["John|Smith|male|Blue|1/1/2000"
           "Mary|Jones|female|Green|1/1/1992"
           "Joe|Blow|male|purple|3/11/1969"]
      2 1 ["John|Smith|male|Blue|1/1/2000"
           "Mary|Jones|female|Green"
           "Joe|Blow|male|purple|3/11/1969"]
      0 0 ""
      0 1 [""])))