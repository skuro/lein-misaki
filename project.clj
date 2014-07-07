(defproject lein-misaki "0.1.5-SNAPSHOT"
  :description "A Leiningen plugin for the Misaki static sites generator"
  :url "https://github.com/skuro/lein-misaki"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[misaki "0.2.6.2-beta"]
                 [gh-file-reader "0.0.3"]
                 [clj-jgit "0.6.3"]
                 [me.raynes/fs "1.4.4"]]
  :eval-in-leiningen true)
