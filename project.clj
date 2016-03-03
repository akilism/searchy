(defproject vice-search "0.1.0-SNAPSHOT"
  :description "Vice Search Prototype"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [compojure "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [clojurewerkz/elastisch "2.2.1"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler vice-search.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
