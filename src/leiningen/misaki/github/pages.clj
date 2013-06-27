(ns leiningen.misaki.github.pages)

(def default-branch "gh-pages")

(defn exists?
  "Checks whether the given branch exists in the current git repository"
  [branch])

(defn copy-to [branch]
  (let [tmpdir (mktemp)
        public (pubdir)]
    (park pubdir temp)
    (checkout branch)
    (restore temp)
    (commit)))

(defn update-pages
  ([] (update-pages "gh-pages"))
  ([branch]
     (if (exists? branch)
       (copy-to branch)
       (do
         (create branch)
         (copy-to branch)))))
