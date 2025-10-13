(ns hkimjp.konpy2.validate
  (:require
   [babashka.fs :as fs]
   [clojure.string :as str]
   [environ.core :refer [env]]
   [jx.java.shell :refer [timeout-sh]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]))

(def ^:private timeout 10)

(defn ruff-path []
  (let [ruff (some #(when (fs/exists? %) %)
                   ["/opt/homebrew/bin/ruff"
                    "/usr/local/bin/ruff"
                    "/snap/bin/ruff"
                    (str (env :home) "/.local/bin/ruff")])]
    (if (some? ruff)
      ruff
      (throw (Exception. "did not find ruff")))))

(defn python-path []
  (let [python (some #(when (fs/exists? %) %)
                     ["/opt/homebrew/bin/python3"
                      "/usr/local/bin/python3"
                      "/usr/bin/python3"
                      (str (env :home) "/workspace/konpy2/.venv/bin/python3")])]
    (if (some? python)
      python
      (throw (Exception. "did not find python3")))))

(defn pytest-path []
  (let [pytest (some #(when (fs/exists? %) %)
                     ["/opt/homebrew/bin/pytest"
                      "/usr/bin/pytest"
                      (str (env :home) "/workspace/konpy2/.venv/bin/pytest")])]
    (if (some? pytest)
      pytest
      (throw (Exception. "did not find pytest")))))

(defn- get-last-answer [author week num]
  (t/log! {:level :debug
           :id "get-last-answer"
           :data {:author author :week week :num num}})
  (let [fetch-answers
        '[:find ?a ?answer
          :in $ ?author ?week ?num
          :where
          [?e :problem/status _]
          [?e :week ?week]
          [?e :num ?num]
          [?a :answer/status "yes"]
          [?a :author ?author]
          [?a :answer ?answer]
          [?a :to ?e]]]
    (try
      (->> (ds/qq fetch-answers author week num)
           (sort-by first)
           last
           second)
      (catch Exception e
        (t/log! {:level :error
                 :id "get-last-answer"
                 :msg (.getMessage e)})))))

(defn- expand-includes
  "expand `#include` recursively."
  [author answer]
  (t/log! :debug "expand-inludes")
  (try
    (str/join
     "\n"
     (for [line (str/split-lines answer)]
       (if-let [[_ _ w n]
                (or (re-matches #"#\s*include\s*(kp)*(\d+)_(\d+).*" line)
                    (re-matches #"from\s*(kp)*(\d+)_(\d+).*" line))]
         (expand-includes
          author
          (get-last-answer author (parse-long w) (parse-long n)))
         line)))
    (catch Exception e
      (t/log! {:level :warn :id "expand-includes" :msg (.getMessage e)})
      (throw (Exception. (.getMessage e))))))

(defn- create-tempfile-with
  "returns fs/file f#object[sun.nio.fs.UnixPath object]"
  [answer]
  (let [f (fs/create-temp-file {:suffix ".py"})]
    (t/log! {:level :debug
             :id "create-tempfile-with"
             :data {:tempfile (str (fs/file f))}})
    (spit (fs/file f) answer)
    f))

(defn- ruff
  "ruff requires '\n' at the end of the code"
  [answer]
  (t/log! {:level :info :id "ruff"})
  (let [f (create-tempfile-with (str answer "\n"))
        ret (timeout-sh
             timeout
             ;; 0.13.*
             ;; (ruff-path "format" "--diff" (str (fs/file f)))
             (ruff-path) "-q" "format" (str (fs/file f)))]
    (if (zero? (:exit ret))
      (fs/delete f)
      (throw (Exception. "using VScode/Ruff?")))))

(defn- has-doctest? [answer]
  (re-find #">>> " (-> answer str/split-lines str/join)))

(defn- doctest [answer]
  (t/log! {:level :info :id "doctest"})
  (when-not (has-doctest? answer)
    (throw (Exception. "did not find a doctest.")))
  (let [f (create-tempfile-with answer)
        ret (timeout-sh
             timeout
             (python-path) "-m" "doctest" (str (fs/file f)))]
    (if (zero? (:exit ret))
      (fs/delete f)
      (throw (Exception. "doctest failed")))))

(defn- pytest [answer testcode]
  (t/log! {:level :info :id "pytest"})
  (t/log! {:level :debug
           :data {:answer answer :testcode testcode}})
  (let [f (create-tempfile-with (str/join [answer "\n" testcode]))
        ret (timeout-sh
             timeout
             (pytest-path) (str (fs/file f)))]
    (if (zero? (:exit ret))
      (fs/delete f)
      (throw (Exception. "pytest failed")))))

(defn validate [author answer testcode]
  (let [answer (expand-includes author answer)]
    (t/log! {:level :info :id "validate" :data {:answer answer}})
    (try
      (ruff answer)
      (doctest answer)
      (when-not (empty? testcode)
        (t/log! {:level :error
                 :data {:testcode testcode :empty? (empty? testcode)}})
        (pytest answer testcode))
      (catch Exception e
        (t/log! {:level :warn
                 :msg   "validate error"
                 :data  {:author author
                         :error (.getMessage e)}})
        (throw (Exception. (.getMessage e)))))))
