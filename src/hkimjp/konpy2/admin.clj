(ns hkimjp.konpy2.admin
  (:require
   [hkimjp.konpy2.response :refer [page]]))

(defn admin [_request]
  (page
   [:div
    [:div "admin only"]
    [:ul
     [:li [:a {:href "/admin/problems"} "problems"]]
     [:li [:a {:href "/admin/new"} "new"]]
     [:li [:a {:href "/admin/update/0"} "edit"]]
     [:li [:a {:href "/admin/delete/0"} "delete!"]]]]))

(defn problems [request]
  (page
   [:div "problems"]))

(defn new [request]
  (page
   [:div "new"]))

(defn create! [request]
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
