(ns leiningen.misaki
  (:use [misaki server config])
  (:require [gh-file-reader.core  :as gh]
            [text-decoration.core :as td]
            [leiningen.misaki.github :as github]))

(defn get-current-directory []
  (-> "."
      java.io.File.
      .getCanonicalPath))

(defn misaki-new
  "Generate a new Misaki project"
  ([project-name]
   (misaki-new project-name "default"))
  ([project-name template-name]
   (println
     (str "Generating a misaki project called " project-name
          " based on the '" template-name "' template."))
   ; download template
   (if-not (gh/download
         (gh/read-content "liquidz" "misaki-showcase"
                          (str "/templates/" template-name "/files"))
         project-name)
     (println (td/red (str "Template '" template-name "' is not found"))))))

(defn ^:no-project-needed misaki
  "Compiles your Misaki sources and starts a local server"
  [project & args]
  (case (first args)
    "new" (apply misaki-new (rest args))
    "listen" (apply github/listen (rest args))
    (apply -main (get-current-directory) args)))
