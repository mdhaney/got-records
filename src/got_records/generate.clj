(ns got-records.generate
  "generate sample data"
  (:require [got-records.specs :as specs]
            [clojure.spec.alpha :as s]
            [clj-time.coerce :as c]
            [clj-time.format :as f]
            [clojure.spec.gen.alpha :as gen]))

(defn sample-data
  "generate n records of sample data using the given delimeter"
  [delim n]
  (let [extract-record (juxt :last-name
                             :first-name
                             (comp specs/gender->string :gender)
                             :favorite-color
                             (comp specs/dob->string :date-of-birth))]
    (->> (gen/sample (s/gen ::specs/person) n)
         (map #(->> (extract-record %)
                    (interpose delim)
                    (apply str)))
         (interpose "\n")
         (apply str))))

(def pipe-sample-data (partial sample-data "|"))
(def space-sample-data (partial sample-data " "))
(def comma-sample-data (partial sample-data ","))

(comment
  (spit "data/data1.txt" (pipe-sample-data 50))
  (spit "data/data2.txt" (space-sample-data 50))
  (spit "data/data3.txt" (comma-sample-data 50)))