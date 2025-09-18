(ns hkimjp.konpy2.stocks
  (:require
   [taoensso.telemere :as t]
   [hkimjp.konpy2.response :refer [page]]))

(defn stocks [request]
  (t/log! {:level :info :id "stocks"})
  (page [:div "scores"]))
