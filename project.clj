(defproject lein-misaki "0.1.2-SNAPSHOT"
  :description "A Leiningen plugin for the Misaki static sites generator"
  :url "https://github.com/skuro/lein-misaki"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[misaki "0.2.3-beta"]
                 [gh-file-reader "0.0.2"]
                 [clj-text-decoration "0.0.1"]
                 [compojure "1.1.3"]
                 [org.clojure/data.json "0.2.0"]
                 [ring/ring-jetty-adapter "1.1.0"]]
  :eval-in-leiningen true)
