(ns got-records.handler
  (:require [liberator.core :refer [resource defresource]]
            [compojure.core :refer [routes ANY]]))


(defn api-routes [component]
  (let [person-service (:person-service component)]
    (routes
      (ANY "/foo" [] (resource :available-media-types ["text/html"]
                               :handle-ok (fn [ctx]
                                            (format "<html>It's %d milliseconds since the beginning of the epoch."
                                                    (System/currentTimeMillis))))))))



