(ns hkimjp.konpy2.util
  (:require
   [clojure.string :as str]
   [java-time.api :as jt]))

(def btn "mx-1 px-1 text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500 rounded")

(def input-box "px-1 border-1 border-solid rounded")

(def start-day (jt/local-date 2025 10 7))

(defn abbrev
  "shorten string s for concise log."
  ([s] (abbrev s 80))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))

(defn user [request]
  (get-in request [:session :identity]))

(defn week
  "Returns how many weeks have passed since the argument `date`.
   If no argument given, the `start`day` defnied above is used."
  ([] (week (jt/local-date)))
  ([date]
   (quot (jt/time-between start-day date :days) 7)))

(defn today []
  (str (jt/local-date)))

(defn now []
  (jt/local-date-time))

; awkward
(defn iso
  "(iso \"2025-09-17T21:32:01.360441\") -> \"2025-09-17 21:32:01\""
  [tm]
  (format "%s %s" (subs tm 0 10) (subs tm 11 19)))


