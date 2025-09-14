(ns hkimjp.konpy2.util
  (:require
   [clojure.string :as str]
   [java-time.api :as jt]))

;(def btn "p-1 rounded text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500")

(def btn "mx-1 px-1 text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500 rounded")

(def input-box "px-1 border-1 border-solid rounded")

(def start-day (jt/local-date 2025 10 7))

(defn week
  "Returns how many weeks have passed since the argument `date`.
   If no argument given, use the `start`day` defnied above."
  ([] (week (jt/local-date)))
  ([date]
   (quot (jt/time-between start-day date :days) 7)))

(defn user [request]
  (get-in request [:session :identity]))

(defn today []
  (str (jt/local-date)))

(defn now []
  (subs (str (jt/local-time)) 0 8))

(defn abbrev
  "shorten string s for concise log."
  ([s] (abbrev s 80))
  ([s n] (let [pat (re-pattern (str "(^.{" n "}).*"))]
           (str/replace-first s pat "$1..."))))
