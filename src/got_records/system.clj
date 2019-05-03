(ns got-records.system
  (:require [com.stuartsierra.component :as component]
            (system.components
              [jetty :refer [new-web-server]]
              [handler :refer [new-handler]]
              [endpoint :refer [new-endpoint]]
              [middleware :refer [new-middleware]])
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [environ.core :refer [env]]
            [got-records.handler :refer [api-routes]]
            [got-records.service :refer [new-person-service]]
            [clojure.edn :as edn]))

(defn dev-system []
  (component/system-map
    :person-service (new-person-service (edn/read-string (env :seed-data)))
    :api (component/using (new-endpoint api-routes) [:person-service])
    :middleware (new-middleware {:middleware [wrap-params
                                              wrap-json-response
                                              #_[wrap-json-body {:keywords? true}]
                                              [wrap-defaults api-defaults]]})
    :handler (component/using (new-handler) [:middleware :api])
    :http (component/using (new-web-server (Integer. (env :http-port))) [:handler])))

; no changes between dev and prod for this simple app
(def prod-system dev-system)