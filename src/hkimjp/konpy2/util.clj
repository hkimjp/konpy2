(ns hkimjp.konpy2.util
  (:require
   [clojure.string :as str]
   [java-time.api :as jt]))

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
