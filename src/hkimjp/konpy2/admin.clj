(ns hkimjp.konpy2.admin
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page]]))

(def btn "p-1 rounded text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500")

(def btn-admin "p-1 rounded-xl text-white bg-red-500 hover:bg-red-700 active:bg-red-900")

(def box "text-center size-10 outline  shadow-lg outline-black/5")

(def te "my-2 p-2 text-md font-mono grow h-60 outline outline-black")

(defn admin [_request]
  (page
   [:div
    [:div.text-2xl.font-bold "admin only"]
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

(defn- div-textarea [label text]
  [:textarea.w-full.h-20.p-2.outline.outline-black.shadow-lg
   {:name label} text])

(defn- section [title]
  [:div.font-bold.pu-4 title])

(defn- input-box [label val]
  [:input.text-center.size-6.outline {:name label :value val}])

(defn- problem-form
  [{:keys [db/id problem/valid week num problem test gpt] :as params}]
  [:div
   [:div.text-2xl.font-bold "Problem-Form"]
   [:form.mx-4 {:method "post"}
    (h/raw (anti-forgery-field))
    [:input {:type "hidden" :name "db/id" :value id}]
    [:input {:type "hidden" :name "problem/valid" :value valid}]
    (section "week-num")
    [:div (input-box "week" week) " - " (input-box "num" num)]
    (section "problem")
    (div-textarea "problem" problem)
    (section "test")
    (div-textarea "test" test)
    (section "gpt")
    (div-textarea "gpt" gpt)
    [:div [:button {:class btn} "create"]]]])

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

(defn create! [{params :params}]
  (t/log! :info (str "create! " params))
  (resp/redirect "/admin/problems"))

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
