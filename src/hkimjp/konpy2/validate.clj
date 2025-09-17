(ns hkimjp.konpy2.validate
  (:require
   [clojure.string :as str]
   [babashka.fs :as fs]
   [jx.java.shell :refer [timeout-sh]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]))

(def ^:private timeout 10)

(def ^:private fetch-answer
  '[:find ?a ?answer
    :in $ ?author ?week ?num
    :where
    [?e :problem/status _]
    [?e :week ?week]
    [?e :num ?num]
    [?a :answer/status "yes"]
    [?a :author ?author]
    [?a :answer ?answer]
    [?a :to ?e]])

(defn- get-last-answer [author week num]
  (t/log! {:level :debug
           :id "get-last-answer"
           :data {:author author :week week :num num}})
  (->> (ds/qq fetch-answer author week num)
       (sort-by first)
       last))

;(get-last-answer "hkimura" 0 1)

(defn- expand-includes
  "expand `#include` recursively."
  [author answer]
  (t/log! :debug "expand-inludes")
  (try
    (str/join
     "\n"
     (for [line (str/split-lines answer)]
       (if-let [[_ w n] (re-matches #"#\s*include\s*(\d+)-(\d+).*" line)]
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

(defn- ruff-path []
  (some #(when (fs/exists? %) %)
        ["/home/ubuntu/.local/bin/ruff"
         "/snap/bin/ruff"
         "/opt/homebrew/bin/ruff"]))

(defn- ruff
  "ruff requires '\n' at the end of the code"
  [answer]
  (t/log! {:level :debug :id "ruff"})
  (let [f (create-tempfile-with (str answer "\n"))
        ret (timeout-sh
             timeout
             (ruff-path) "format" "--diff" (str (fs/file f)))]
    (if (zero? (:exit ret))
      (fs/delete f)
      (throw (Exception. "using VScode/Ruff?")))))

(defn- python-path []
  (some #(when (fs/exists? %) %)
        ["/usr/local/bin/python3"
         "/opt/homebrew/bin/python3"
         "/usr/bin/python3"]))

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

;---------------------
(defn- pytest [answer]
  (t/log! {:level :info :id "pytest"}))

;-----------------------------
(defn validate [author answer]
  (let [answer (expand-includes author answer)]
    (t/log! :info "validate")
    (t/log! {:level :info :data {:answer answer}})
    (try
      (ruff answer)
      (doctest answer)
      (pytest answer)
      (catch Exception e
        (t/log! {:level :warn
                 :msg "validate error"
                 :data {:author author
                        :error (.getMessage e)}})
        (throw (Exception. e))))))
