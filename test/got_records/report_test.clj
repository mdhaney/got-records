(ns got-records.report-test
  (:require [clojure.test :refer :all]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [got-records.specs :as spec]
            [got-records.report :as nut]
            [clojure.spec.alpha :as s]
            [got-records.specs :as specs]))

;; generate 1000 random records and convert to report lines
(defspec person->report-line-tests
  1000
  (prop/for-all [person (s/gen ::spec/person)]
    (let [expected (->> person
                        specs/extract-fields
                        (interpose " ")
                        (apply str))]
      (= expected (nut/person->report-line person)))))

(def sort-data [{:id 1 :last-name "C" :gender :male :date-of-birth #inst "1950-01-01"}
                {:id 2 :last-name "D" :gender :male :date-of-birth #inst "1900-02-01"}
                {:id 3 :last-name "Q" :gender :female :date-of-birth #inst "1900-01-10"}
                {:id 4 :last-name "Z" :gender :male :date-of-birth #inst "1900-10-01"}
                {:id 5 :last-name "B" :gender :male :date-of-birth #inst "1930-01-01"}
                {:id 6 :last-name "G" :gender :female :date-of-birth #inst "1920-01-01"}
                {:id 7 :last-name "E" :gender :female :date-of-birth #inst "1900-03-01"}
                {:id 8 :last-name "F" :gender :female :date-of-birth #inst "1910-01-01"}
                {:id 9 :last-name "A" :gender :male :date-of-birth #inst "1900-01-01"}])

(deftest sorting-tests
  (testing "sorting by gender, then last name ascending"
    (is (= (mapv :id (nut/sort-by-gender sort-data))
           [7 8 6 3 9 5 1 2 4])))
  (testing "sorting by birth date, ascending"
    (is (= (mapv :id (nut/sort-by-birthdate sort-data))
           [9 3 2 7 4 8 6 5 1])))
  (testing "sorting by last name, descending"
    (is (= (mapv :id (nut/sort-by-last-name-desc sort-data))
           [4 3 6 8 7 2 1 5 9]))))