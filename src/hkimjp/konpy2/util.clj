(ns hkimjp.konpy2.util
  (:require
   [clojure.string :as str]
   [environ.core :refer [env]]
   [java-time.api :as jt]))

(def btn "mx-1 px-1 text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500 rounded")

(def input-box "px-1 border-1 rounded")

(defn abbrev
  "shorten string s for concise log."
  ([s] (abbrev s 80))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

(defn user [request]
  (get-in request [:session :identity]))

(def start-day
  (if-let [d (env :start-day)]
    (let [[_ year month date] (re-find #"(\d{4})-(\d{2})-(\d{2})" d)]
      (jt/local-date (parse-long year)
                     (parse-long month)
                     (parse-long date)))
    (jt/local-date 2025 10 1))) ; production 2025 10 1

; 2025 9 22

(defn week
  "Returns how many weeks have passed since the argument `date`.
   If no argument given, the `start`day` defnied above is used."
  ([] (week (jt/local-date)))
  ([date]
   (quot (jt/time-between start-day date :days) 7)))

; not orthogonal
(defn today []
  (str (jt/local-date)))

(defn now []
  (jt/local-date-time))

(defn local-date []
  (str (jt/local-date)))

(defn local-date-time []
  (str (jt/local-date-time)))

; awkward
(defn iso
  "(iso \"2025-09-17T21:32:01.360441\") -> \"2025-09-17 21:32:01\""
  [tm]
  (format "%s %s" (subs tm 0 10) (subs tm 11 19)))

(defn last-col
  "returns the last column of `line` seperated by spaces" []
  [line]
  (loop [line (read-line)]
    (when (some? line)
      (println (-> line
                   (str/split #"\s+")
                   last))
      (recur (read-line)))))
