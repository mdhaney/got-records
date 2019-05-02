(ns got-records.report
  (:require [got-records.specs :as specs]))

(defn sort-by-birthdate
  "takes a list of person records and sorts by birthdate in ascending order"
  [xs]
  (sort-by :date-of-birth xs))

(defn sort-by-last-name-desc
  "takes a list of person records and sorts by last name in descending order"
  [xs]
  (sort-by :last-name #(compare %2 %1) xs))

(defn sort-by-gender
  "takes a list of person records and sorts by gender, female first.
   within each gender, sort by last name ascending"
  [xs]
  (sort-by (juxt :gender :last-name) xs))

(defn person->report-line
  "takes a person record and returns a single string to be used
   as a line in a report"
  [person]
  (->> person
       specs/extract-fields
       (interpose " ")
       (apply str)))

(defn output-header [title]
  (println title)
  (println "--------"))

(defn output-lines [data]
  (doseq [line (map person->report-line data)]
    (println line)))

(defn output-report [title sort-fn data]
  (output-header title)
  (output-lines (sort-fn data)))

(def report-output-1 (partial output-report "Output 1" sort-by-gender))
(def report-output-2 (partial output-report "Output 2" sort-by-birthdate))
(def report-output-3 (partial output-report "Output 3" sort-by-last-name-desc))
