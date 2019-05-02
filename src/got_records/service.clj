(ns got-records.service
  "A simple service to manage person records in an atom"
  (:require [com.stuartsierra.component :as component]
            [got-records.import :refer [import-all]]))

;;
;; component
;;
(defrecord PersonService [seed data]
  component/Lifecycle
  (start [this]
    (let [seed-data (import-all seed)]
      (println "Finished loading seed data")
      (reset! data seed-data)
      this))
  (stop [this]
    (reset! data [])
    this))

(defn new-person-service [seed-files]
  (map->PersonService {:seed seed-files :data (atom [])}))