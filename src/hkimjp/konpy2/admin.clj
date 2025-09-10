(ns hkimjp.konpy2.admin
  (:require
   [hkimjp.konpy2.response :refer [page]]))

(defn admin [_request]
  (page
   [:div "admin only"]))

(defn list [request]
  (page
   [:div "list"]))

(defn new [request]
  (page
   [:div "new"]))

(defn create! [reqest]
  (page
   [:div "create!"]))

(defn edit [request]
  (page
   [:div "edit"]))

(defn update! [request]
  (page
   [:div "update!"]))

(defn delete! [request]
  (page [:div "delete!"]))
