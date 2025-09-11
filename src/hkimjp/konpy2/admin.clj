(ns hkimjp.konpy2.admin
  (:require
   [taoensso.telemere :as t]
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

(defn upsert [])

(defn create! [params :params]
  (t/log! :info "create! params)
  (page
   [:div "create!"]))

(def ta-class "w-180 h-10 p-2 outline outline-black/5 shadow-lg")

(defn- problem-form
  [{:keys [db/id
           problem/valid
           week
           num
           problem
           test
           gpt] :as params}]
  [:form {:method "post" :action "/admin/create"}
   [:input {:type "hidden" :name "db/id" :value id}]
   [:input {:type "hidden" :name "problem/valid" :value valid}]
   [:div
    [:input {:name "week" :value week}] "-" [:input {:name "num" :value num}]]
   [:div [:textarea {:class ta-class :name "problem"} problem]]
   [:div [:textarea {:class ta-class :name "test"} test]]
   [:div [:textarea {:class ta-class :name "gpt"} gpt]]
   [:button "create"]])

(defn new [request]
  (t/log! :info "new")
  (page
   (problem-form {:db/id -1
                  :problem/valid true
                  :week ""
                  :num ""
                  :problem ""
                  :test ""
                  :gpt ""})))

(defn problems [request]
  (page
   [:div
    [:div.text-2xl.font-bold "Problems"]
    [:a.hover:underline {:href "/admin/new"} "new"]
    [:div "LIST"]]))

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
