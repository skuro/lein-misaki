(ns leiningen.misaki
  (:use [misaki server config]
        [cemerick.pomegranate :only [add-dependencies]])
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

(defn load-misaki-deps
  "Loads the misaki dependencies as specified by the :misaki-dependency entry
   in your project.clj or profiles.clj"
  [deps]
  (println (str "Loading the following misaki plugins:"))
  (doseq [dep deps]
    (apply println dep))
  (add-dependencies :coordinates deps
                    :repositories {"clojars" "http://clojars.org/repo"}))

(defn ^:no-project-needed misaki
  "Compiles your Misaki sources and starts a local server"
  [project & args]
  (if-let [deps (:misaki-dependencies project)]
    (load-misaki-deps deps))
  (case (first args)
    "new" (apply misaki-new (rest args))
    "listen" (apply github/listen (rest args))
    (apply -main (get-current-directory) args)))
