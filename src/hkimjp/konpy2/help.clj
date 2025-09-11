(ns hkimjp.konpy2.help
  (:require
   [hkimjp.konpy2.response :refer [page]]))

(defn help
  [_request]
  (page
   [:div.mx-4
    [:div.text-2xl.font-medium "Help"]
    [:p.mx-4 "under construction"]]))

