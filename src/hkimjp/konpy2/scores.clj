(ns hkimjp.konpy2.scores
  (:require
   [taoensso.telemere :as t]
   [hkimjp.konpy2.response :refer [page]]
   [hkimjp.konpy2.util :refer [user]]))

(defn scores [request]
  (t/log! {:level :info :msg (str "scores " (user request))})
  (page
   [:div.m-4
    [:div.text-2xl "Scores"]
    [:p "under construction"]]))

