(ns leiningen.misaki
  (:use [misaki server config]))

(defn get-current-directory []
  (-> "."
      java.io.File.
      .getCanonicalPath))

(defn ^:no-project-needed misaki
  "Compiles your Misaki sources and starts a local server"
  [project & args]
  (apply -main (get-current-directory) args))