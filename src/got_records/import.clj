(ns got-records.import
  (:require [clojure.string :refer [split]]
            [clojure.java.io :as io]
            [got-records.specs :as spec]
            [clojure.spec.alpha :as s]))

(defn split-line
  "splits a string representing a line (record) in the input file along one of
   the supported delimiters.  If no delimiter is found, returns nil"
  [line]
  {:pre [(string? line)]}
  (let [result (split line #" |,|\|")]
    (when-not (= result [line])
      result)))

(defn line->record
  "convert an input line to a map record using the given keys"
  [keys line]
  {:pre [(vector? keys)
         (every? keyword? keys)]}
  (when-let [data (split-line line)]
    (zipmap keys data)))

(def person-keys [:last-name :first-name :gender :favorite-color :date-of-birth])

(defn line->person
  "converts an input line to a person record that matches the spec or :clojure.spec.alpha/invalid"
  [line]
  (try
    (s/conform ::spec/person
               (-> (line->record person-keys line)
                   (update-in [:gender] spec/string->gender)
                   (update-in [:date-of-birth] spec/string->dob)))
    (catch AssertionError e ::s/invalid)))

(defn import-seq
  "from the given sequence, convert each line to person records.  returns a map
   with the following keys:
       :data - a list of imported records
       :skipped - number of records which were skipped/dropped because of errors"
  [iseq]
  (->> iseq
       (map line->person)
       (reduce (fn [acc item]
                 (let [acc (update-in acc [:read] inc)]
                   (if (= item ::s/invalid)
                     (update-in acc [:skipped] inc)
                     (update-in acc [:data] conj item))))
               {:data [] :skipped 0 :read 0})))

(defn import-file
  "given a filename, import all the records from that file"
  [fname]
  (try
    (with-open [rdr (io/reader fname)]
      (import-seq (line-seq rdr)))
    (catch Exception e {:data [] :skipped 0 :read 0})))

(defn import-all
  "import and combine the results of all the given files"
  [files]
  (->> files
       (map import-file)
       (reduce (fn [acc item]
                 (-> acc
                     (update-in [:data] concat (:data item))
                     (update-in [:skipped] + (:skipped item))
                     (update-in [:read] + (:read item))))
               {:data [] :skipped 0 :read 0})))

(def sample-files ["data/data1.txt" "data/data2.txt" "data/data3.txt"])
