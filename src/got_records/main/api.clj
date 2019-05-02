(ns got-records.main.api
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [got-records.system :refer [prod-system]]))

(defn -main
  "
  Start a production system
  "
  [& args]
  (component/start (prod-system)))
