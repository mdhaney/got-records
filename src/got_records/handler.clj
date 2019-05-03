(ns got-records.handler
  (:require [compojure.core :refer [routes ANY GET POST]]
            [compojure.route :as r]
            [got-records.service :as ps]))

(defn json-response [data & [status]]
  {:status  (or status 200)
   :headers {"Content-Type" "application/json"}
   :body    data})

(defn gender-report-handler [person-service req]
  (json-response (ps/report-by-gender person-service)))

(defn birthdate-report-handler [person-service req]
  (json-response (ps/report-by-birthdate person-service)))

(defn name-report-handler [person-service req]
  (json-response (ps/report-by-name person-service)))

(defn add-record-handler [person-service req]
  (let [body (slurp (:body req))
        record (ps/add-person person-service body)]
    (if (nil? record)
      (json-response {:error "Error creating record"} 400)
      (json-response record 201))))

(defn api-routes [component]
  (let [person-service (:person-service component)]
    (routes
      (POST "/records" [] (partial add-record-handler person-service))

      (GET "/records/gender" [] (partial gender-report-handler person-service))
      (GET "/records/birthdate" [] (partial birthdate-report-handler person-service))
      (GET "/records/name" [] (partial name-report-handler person-service))

      (ANY "/*" [] (r/not-found "Invalid route")))))



