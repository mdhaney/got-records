(ns got-records.service
  "A simple service to manage person records in an atom"
  (:require [com.stuartsierra.component :as component]
            [got-records.import :as imp]
            [got-records.report :as report]
            [clojure.spec.alpha :as s]))

;;
;; protocol to define how we interact with the dataset
;;
(defprotocol PersonDb
  (report-by-gender [this]
    "return all records sorted by gender, female first, then last name asc")
  (report-by-birthdate [this]
    "return all records sorted by birthdate")
  (report-by-name [this]
    "return all records sorted by last name desc")
  (add-person [this data]
    "add a person; data is in one of the 3 supported import formats.
    returns the new person record, or nil if there was a problem"))

;;
;; component
;;
(defrecord PersonService [seed db]

  component/Lifecycle
  (start [this]
    (let [{:keys [data read skipped]} (imp/import-all seed)]
      (when (> skipped 0)
        (println "Skipped" skipped "records in seed data."))
      (println "Finished loading" (- read skipped) "records from seed data.")
      (reset! db data)
      this))
  (stop [this]
    (reset! db [])
    this)

  PersonDb
  (report-by-gender [_]
    (report/sort-by-gender @db))
  (report-by-birthdate [_]
    (report/sort-by-birthdate @db))
  (report-by-name [_]
    (report/sort-by-last-name-desc @db))
  (add-person [_ data]
    (println "add-person data: " data (string? data))
    (let [result (imp/line->person data)]
      (println "add-person result: " result)
      (if (= result ::s/invalid)
        nil
        (do
          (swap! db conj result)
          result)))))

(defn new-person-service [seed-files]
  (map->PersonService {:seed seed-files :db (atom [])}))