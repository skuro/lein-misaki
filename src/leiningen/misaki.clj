(ns leiningen.misaki
  (:use [misaki server config]))

(defn get-current-directory []
  (-> "."
      java.io.File.
      .getCanonicalPath))

(defn ^:no-project-needed misaki
  "I don't do a lot."
  [project & args]
  (apply -main (get-current-directory) args))