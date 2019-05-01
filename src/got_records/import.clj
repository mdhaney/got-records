(ns got-records.import
  (:require [clojure.string :refer [split]]
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