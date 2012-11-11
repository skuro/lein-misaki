(ns leiningen.misaki.github
  (:use compojure.core
        ring.adapter.jetty
        clojure.java.io)
  (:require [compojure.handler :as handler]
            [clojure.data.json :as json]
            [clojure.java.shell :as sh]
            [misaki.server :as misaki]
            [misaki.config :as config]))

(defonce update-queue (java.util.concurrent.LinkedBlockingDeque.))

(def config (atom {}))

(defn create-temp-folder []
  (let [tmpdir (System/getProperty "java.io.tmpdir")]
    (-> (sh/sh "mktemp" "-d" (str tmpdir "misaki.XXXX"))
       :out
       (.replaceAll "\n" ""))))

(defn compile-from [repodir]
  ; cut-n-paste from misaki.server/-main
  (binding [misaki.config/*base-dir* repodir]
    (config/with-config
      ; override the destination dir, if needed
      (if-let [todir (:destdir @config)]
        (binding [misaki.config/*config* (assoc misaki.config/*config* :public-dir todir)]
          (misaki/do-all-compile))
        (misaki/do-all-compile)))))

(defn prepare-sources [tmpdir repo branch all-done]
  (if-let [branch (:branch @config)]
    (sh/sh "git" "clone" repo "--branch" branch tmpdir)
    (sh/sh "git" "clone" repo tmpdir))
  (compile-from (str tmpdir "/"))
  (deliver all-done true))

(defn checkout-sources [repo]
  (let [tmpdir (create-temp-folder)
        all-done (promise)]
    (try
      (println "Working dir: " tmpdir)
      (prepare-sources tmpdir repo (:branch @config) all-done)
      (finally
       (if (.exists (file tmpdir))
         (do
           (deref all-done 10000 false)
           (sh/sh "rm" "-Rf" tmpdir)))))))

(defn poll-updates []
  (while true
    (let [url (.take update-queue)]
      (println "Refreshing from repository: " url))))

(defonce worker-tread (doto
                          (Thread. poll-updates)
                        (.setDaemon true)
                        (.start)))

(defn refresh-site [payload]
  (let [url (get-in payload [:repository :url])]
    (if (.offer update-queue url)
    (str "A new update has been scheduled: " url "\n")
    "Can't schedule a new update, please try again later")))

(defn process-payload [payload]
  (if (nil? payload)
    "Payload is null, aborting."
    (let [mpayload (json/read-str payload :key-fn keyword)]
      (refresh-site mpayload))))

(defroutes github-listeners
  (POST "/post-commit" [payload] (process-payload payload))
  (GET "/manual-update" [] (refresh-site {:manual true})))

(def github-listeners-app
  (-> github-listeners
      handler/site))

(defn listen
  ([] (listen nil nil))
  ([destdir branch]
     (reset! config {:destdir destdir :branch branch})
     (run-jetty github-listeners-app {:port 9090})))

