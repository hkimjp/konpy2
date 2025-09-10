(ns hkimjp.konpy2.admin
  (:require
   [hkimjp.konpy2.response :refer [page]]))

(defn admin
  [_request]
  (page
   [:div "admin only"]))
