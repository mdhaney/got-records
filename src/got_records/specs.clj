(ns got-records.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.java.io :as io]
            [clojure.string :refer [upper-case]]
            [clj-time.coerce :as c]
            [clj-time.format :as f]
            [clojure.edn :as edn]))

(defn alphanumeric?
  "predicate to test if a string only contains alphanumeric characters"
  [s]
  {:pre [(string? s)]}
  (some? (re-matches #"\w*" s)))

(defn gen-from-resource
  "Create a generator from a resource file"
  [f]
  (let [values (edn/read-string (slurp (io/resource f)))]
    (fn []
      (s/gen values))))

(s/def :person/last-name (s/with-gen
                           (s/and string? alphanumeric?)
                           (gen-from-resource "surnames.edn")))

(def female-gen (gen-from-resource "female.edn"))
(def male-gen (gen-from-resource "male.edn"))
(s/def :person/first-name (s/and string? alphanumeric?))

(s/def :person/favorite-color (s/with-gen
                          (s/and string? alphanumeric?)
                          (gen-from-resource "colors.edn")))

(s/def :person/gender #{:female :male})

(s/def :person/date-of-birth (s/inst-in #inst "1900" #inst "2002"))

(s/def ::person* (s/keys :req-un [:person/last-name :person/favorite-color :person/date-of-birth]))
(s/def ::person-gender* (s/with-gen
                          (s/keys :req-un [:person/gender :person/first-name])
                          #(gen/frequency [[50 (gen/hash-map :gender (gen/return :male) :first-name (male-gen))]
                                           [50 (gen/hash-map :gender (gen/return :female) :first-name (female-gen))]])))

(s/def ::person (s/merge ::person* ::person-gender*))

;;
;; data conversion helpers
;;
(def date-formatter (f/formatter "M/d/YYYY"))

(defn dob->string
  "takes a DOB as an inst and outputs a formatted string for the date"
  [dob]
  {:pre [(inst? dob)]}
  (->> dob
       c/from-date
       (f/unparse date-formatter)))

(defn string->dob
  "parses a date string into an inst.  returns nil if unable to parse"
  [dob-str]
  {:pre [(string? dob-str)]}
  (try
    (->> dob-str
         (f/parse date-formatter)
         c/to-date)
    (catch IllegalArgumentException e nil)))

(defn gender->string
  "takes a gender keyword and outputs the string representation"
  [gender]
  {:pre [(keyword? gender)]}
  (name gender))

(def allowed-male #{"MALE" "M"})
(def allowed-female #{"FEMALE" "F"})
(defn string->gender
  "takes a string and outputs the correct gender keyword, or nil"
  [gender-str]
  {:pre [(string? gender-str)]}
  (let [gender (upper-case gender-str)]
    (cond
      (contains? allowed-male gender) :male
      (contains? allowed-female gender) :female)))

;; helper to extract all the fields of a person record into a vector in the
;; correct order for building reports
(def extract-fields (juxt :last-name
                          :first-name
                          (comp gender->string :gender)
                          :favorite-color
                          (comp dob->string :date-of-birth)))

