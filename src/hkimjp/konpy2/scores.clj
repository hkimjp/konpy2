(ns hkimjp.konpy2.scores
  (:require
   [taoensso.telemere :as t]
   [hkimjp.konpy2.response :refer [page]]))

(defn scores [request]
  (t/log! {:level :info :id "scores"})
  (page [:div "scores"]))
