(ns got-records.specs
  (:require [clojure.spec.alpha :as s]))

(defn alphanumeric?
  "predicate to test if a string only contains alphanumeric characters"
  [s]
  {:pre [(string? s)]}
  (some? (re-matches #"\w*" s)))

(s/def ::last-name (s/and string? alphanumeric?))
(s/def ::first-name (s/and string? alphanumeric?))

(def allowed-genders #{"Female" "Male"})
(s/def ::gender (s/and string? allowed-genders))

(def allowed-colors #{"Red" "Yellow" "Green" "Blue" "Brown" "Black" "Orange" "Purple"})
(s/def ::favorite-color (s/and string? allowed-colors))

(s/def ::date-of-birth (s/inst-in #inst "1900" #inst "2002"))

(s/def ::person (s/keys :req-un [::last-name ::first-name ::gender ::favorite-color ::date-of-birth]))