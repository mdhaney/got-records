(ns got-records.main.import
  (:gen-class)
  (:require [got-records.import :refer [import-all]]
            [got-records.report :as r]
            [clojure.java.io :as io]))

(def sample-files ["data/data1.txt" "data/data2.txt" "data/data3.txt"])

(defn print-usage []
  (println "Usage:")
  (println "")
  (println "1st argument - report type:")
  (println "   1 - sorted by gender")
  (println "   2 - sorted by birth date, ascending")
  (println "   3 - sorted by last name, descending")
  (println "")
  (println "Each additional argument is the path to a file to import.")
  (println "If no file arguments are given, the sample files in the project /data directory will be used.")
  (println ""))

(defn validate-files
  "validates the given input files and returns a map for each file with keys:
       :valid (true / false)
       :file (passed filename"
  [filenames]
  (map (fn [n] {:file n :valid (.exists (io/file n))}) filenames))

(defn report-file-errors [validated-files]
  (doseq [{:keys [valid file]} validated-files]
    (when-not valid
      (println "File" file "could not be found."))))

(defn process [report-type files]
  (println "Processing the following files:")
  (doseq [filename files]
    (println "   " filename))
  (let [{:keys [data read skipped]} (import-all files)]
    (println "Read " read " records.")
    (println "Skipped " skipped " records.")
    (cond
      (= "1" report-type) (r/report-output-1 data)
      (= "2" report-type) (r/report-output-2 data)
      (= "3" report-type) (r/report-output-3 data))))

(defn -main [& [report-type & in-files]]
  (let [files (if in-files in-files sample-files)]
    (if report-type
      (if (contains? #{"1" "2" "3"} report-type)
        (let [validated-files (validate-files files)
              filenames (map :file validated-files)
              all-valid? (every? true? (map :valid validated-files))]
          (if all-valid?
            (process report-type filenames)
            (report-file-errors validated-files)))
        (print-usage))
    (print-usage))))