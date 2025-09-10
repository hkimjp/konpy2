(ns hkimjp.konpy2.admin
  (:require
   [hkimjp.konpy2.view :refer [page]]))

(defn admin
  [_request]
  (page
   [:div "admin"]))
