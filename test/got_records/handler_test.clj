(ns got-records.handler-test
  (:require [clojure.test :refer :all]
            [com.stuartsierra.component :as component]
            [got-records.service :as ps]
            [got-records.handler :as nut]
            [ring.mock.request :as mock]))

(deftest route-tests
  (let [person-service (component/start (ps/new-person-service ["data/test.txt"]))
        handler (nut/api-routes {:person-service person-service})]

    (testing "gender report"
      (let [response (handler (mock/request :get "/records/gender"))]
        (is (= 200 (:status response)))
        (is (= 10 (count (:body response))))
        (is (= (mapv :last-name (:body response))
               ["Cooley" "Gould" "Miranda" "Oneal" "Schneider" "Best" "Goodman" "Gould" "Moyer" "Olson"]))))

    (testing "birthdate report"
      (let [response (handler (mock/request :get "/records/birthdate"))]
        (is (= 200 (:status response)))
        (is (= 10 (count (:body response))))
        (is (= (mapv :last-name (:body response))
               ["Best" "Cooley" "Moyer" "Oneal" "Olson" "Schneider" "Miranda" "Goodman" "Gould" "Gould"]))))

    (testing "name report"
      (let [response (handler (mock/request :get "/records/name"))]
        (is (= 200 (:status response)))
        (is (= 10 (count (:body response))))
        (is (= (mapv :last-name (:body response))
               ["Schneider" "Oneal" "Olson" "Moyer" "Miranda" "Gould" "Gould" "Goodman" "Cooley" "Best"]))))

    (testing "add person"
      (let [response (handler (-> (mock/request :post "/records")
                                  (mock/body "Lannister,Tyrian,male,red,6/6/1960")))]
        (is (= 201 (:status response)))
        (let [response (handler (mock/request :get "/records/name"))]
          (is (= 200 (:status response)))
          (is (= 11 (count (:body response))))
          (is (= (mapv :last-name (:body response))
                 ["Schneider" "Oneal" "Olson" "Moyer" "Miranda" "Lannister" "Gould" "Gould" "Goodman" "Cooley" "Best"])))))

    (testing "add failure"
      (let [response (handler (-> (mock/request :post "/records")
                                  (mock/body "We can't read this")))]
        (is (= 400 (:status response)))))))