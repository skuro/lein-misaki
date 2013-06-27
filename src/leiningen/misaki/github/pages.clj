(ns leiningen.misaki.github.pages
  (:require [me.raynes.fs :as fs]
            [clj-jgit.porcelain :as git]
            [misaki.config :as cfg]))

(def default-branch "gh-pages")

(declare ^:dynamic *git-repo*)

(defn exists?
  "Checks whether the given branch exists in the current git repository"
  [branch]
  (->> (git/git-branch-list *git-repo*)
       (map #(.getName %))
       (filter #{(str "refs/heads/" branch)})
       seq))

(defn mktemp
  "Creates a temporary folder name"
  []
  (fs/temp-name "misaki"))

(defmacro with-temp
  "Executes the body making sure the temporary directory is removed at the end"
  [& body]
  (let [tmpname (mktemp)
        tmppath (.getPath (fs/file (fs/tmpdir) tmpname))]
    `(try
       (let [~'tmpdir (fs/mkdir ~tmppath)]
         ~@body)
       (finally
         (fs/delete-dir ~tmppath)))))

(defn pubdir
  "Returns a file path of the directory containing the public files"
  []
  (cfg/with-config
    (cfg/public-path "")))

(defn copy-files
  "Copy the content of the 'from' directory to 'to'. Both directories must exist."
  [from to]
  (when (and (fs/exists? from)
             (fs/exists? to))
    (if (or (fs/file? from)
            (fs/file? to))
      (throw (IllegalArgumentException. (str to " is a file")))
      (let [from (fs/file from)
            to (fs/file to)
            trim-size (-> from str count inc)
            dest #(fs/file to (subs (str %) trim-size))]
        (dorun
         (fs/walk (fn [root dirs files]
                 (doseq [dir dirs]
                   (when-not (fs/directory? dir)
                     (-> root (fs/file dir) dest fs/mkdirs)))
                 (doseq [f files]
                   (fs/copy+ (fs/file root f) (dest (fs/file root f)))))
               from))
        to))))

(defn park
  "Copies the full content of the source dir into the dest dir"
  [from to]
  (copy-files from to))

(defn checkout
  "Checks out the provided git branch"
  [branch]
  (git/git-checkout *git-repo* branch))

(defn restore
  "Restores the parked data into the current folder"
  [tmpdir]
  (copy-files tmpdir fs/*cwd*))

(defn commit
  "Adds and commits all the new content into the current branch"
  []
  (let [status (git/git-status *git-repo*)
        files  (reduce into #{} [(:untracked status)
                                 (:modified  status)])]
    (doseq [file files]
      (git/git-add *git-repo* file))
    (git/git-commit *git-repo* "[lein-misaki] updates committed")))

(defn current-branch
  "Gets the current branch name"
  []
  (git/git-branch-current *git-repo*))

(defn create
  "Creates a new branch"
  [branch]
  (git/git-branch-create *git-repo* branch))

(defn copy-to [branch]
  (with-temp
   (let [public (pubdir)
         branch-orig (current-branch)]
     (park pubdir tmpdir)
     (checkout branch)
     (restore tmpdir)
     (commit)
     (checkout branch-orig))))

(defn update
  ([] (update "gh-pages"))
  ([branch]
     (binding [*git-repo* (load-repo (.getPath fs/*cwd*))]
      (if (exists? branch)
        (copy-to branch)
        (do
          (create branch)
          (copy-to branch))))))
