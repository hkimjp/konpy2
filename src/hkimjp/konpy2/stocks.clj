(ns hkimjp.konpy2.stocks
  (:require
   [taoensso.telemere :as t]
   [hkimjp.konpy2.response :refer [page]]
   [hkimjp.konpy2.util :refer [user]]))

(defn stocks [request]
  (t/log! {:level :info :msg (str "stocks " (user request))})
  (page [:div
         [:div.m-4
          [:div.text-2xl "Stocks"]
          [:p "under construction"]]]))
