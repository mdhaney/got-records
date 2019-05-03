(defproject got-records "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [clj-time "0.15.0"]
                 [com.stuartsierra/component "0.4.0"]
                 [ring "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.4.0"]
                 [compojure "1.6.1"]
                 [org.danielsz/system "0.4.2"]
                 [environ "1.1.0"]]

  :plugins [[lein-environ "1.1.0"]]

  :profiles {:dev {:source-paths ["dev"]
                   :env {:http-port 8000
                         :seed-data ["data/data1.txt"
                                     "data/data2.txt"
                                     "data/data3.txt"]}
                   :dependencies [[org.clojure/test.check "0.9.0"]
                                  [org.clojure/tools.namespace "0.2.11"]
                                  [com.stuartsierra/component.repl "0.2.0"]
                                  [ring/ring-mock "0.3.2"]]
                   :repl-options {:init-ns user}}

             :prod {:env {:http-port 8888
                          :seed-data []}}

             :uberjar {:aot :all
                       :main got-records.main.api}

             :import {:main got-records.main.import}}

  :aliases {"import" ["with-profile" "import" "do" "run"]})
