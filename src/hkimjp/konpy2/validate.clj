(ns hkimjp.konpy2.validate
  (:require
   [clojure.string :as str]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]))

(def ^:pricate fetch-answer
  '[:find ?answer
    :in $ ?author ?week ?num
    :where
    [?e :problem/status _]
    [?e :week ?week]
    [?e :num ?num]
    [?a :answer/status "yes"]
    [?a :author ?author]
    [?a :answer ?answer]
    [?a :to ?e]])

; (ffirst (ds/qq fetch-answer "hkimura" 0 0))

(defn get-answer [author week num]
  (-> (ds/qq fetch-answer author week num)
      ffirst))

(defn expand-includes
  "expand `#include` recursively."
  [author answer]
  (try
    (str/join
     "\n"
     (for [line (str/split-lines answer)]
       (if-let [[_ w n] (re-matches #"#\s*include\s*(\d+)-(\d+).*" line)]
         (expand-includes author (get-answer author
                                             (parse-long w)
                                             (parse-long n)))
         line)))
    (catch Exception e
      (t/log! {:level :error :id "expand-includes" :msg (.getMessage e)})
      (throw (Exception. (.getMessage e))))))

; (expand-includes "hkimura" "#include 0-0
; hello world")

(defn ruff [answer])

(defn doctest [answer])

(defn pytest [answer])

(defn validate [author answer]
  (let [answer (expand-includes author answer)]
    ; (t/log! :debug answer)
    (try
      (ruff answer)
      (doctest answer)
      (pytest answer)
      true
      (catch Exception e
        (throw (Exception. e))))))
