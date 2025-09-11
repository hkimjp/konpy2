(ns hkimjp.konpy2.admin
  (:require
   [taoensso.telemere :as tel]
   [hkimjp.datascript :as ds]
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

(def get-problems
  '[:find ?e ?week ?num ?problem ?test ?gpt ?updated
    :where
    [?e :probrem/valie true]
    [?e :week ?week]
    [?e :num ?num]
    [?e :problem ?problem]
    [?e :test ?test]
    [?e :gpt ?gpt]
    [?e :updated ?updated]])

(ds/qq get-problems)

(defn upsert [])

(defn- problem-form
  [{:keys [db/id
           problem/valid
           week
           num
           problem
           test
           gpt] :as params}]
  [:form
   (str params)
   [:button]])

(defn div-new []
  (problem-form {:db/id -1
                 :problem/valid ""
                 :week ""
                 :num ""
                 :problem ""
                 :test ""
                 :gpt ""}))

(defn problems [request]
  (page
   [:div
    [:div.text-2xl.font-bold "Problems"]
    [:a {:href "/admin/new"} [:span.hover:underline "new"]]
    [:div "LIST"]]))

(defn create! [request]
  (page
   [:div "create!"]))

(def q-pr '[:find ?e
            :where
            [?e :kp2/problem _]])

(defn edit [request]
  (page
   [:div "edit"]))

(defn update! [request]
  (page
   [:div "update!"]))

(defn delete! [request]
  (page [:div "delete!"]))
