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
  (->> (gen/sample (s/gen ::specs/person) n)
       (map #(->> (specs/extract-fields %)
                  (interpose delim)
                  (apply str)))
       (interpose "\n")
       (apply str)))

(def pipe-sample-data (partial sample-data "|"))
(def space-sample-data (partial sample-data " "))
(def comma-sample-data (partial sample-data ","))

(comment
  (spit "data/data1.txt" (pipe-sample-data 50))
  (spit "data/data2.txt" (space-sample-data 50))
  (spit "data/data3.txt" (comma-sample-data 50)))