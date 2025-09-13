(ns hkimjp.konpy2.admin
  (:require
   [java-time.api :as jt]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page]]
   [hkimjp.konpy2.util :refer [btn user]]))

; (def btn "p-1 rounded text-white bg-sky-500 hover:bg-sky-700 active:bg-red-500")

(defn admin [_request]
  (page
   [:div
    [:div.text-2xl.font-bold "admin only"]
    [:ul
     [:li [:a {:href "/admin/problems"} "problems"]]
     [:li [:a {:href "/admin/new"} "new"]]
     [:li [:a {:href "/admin/update/0"} "edit"]]
     [:li [:a {:href "/admin/delete/0"} "delete!"]]]]))

(defn- div-textarea [label text]
  [:textarea.w-full.h-20.p-2.outline.outline-black.shadow-lg
   {:name label} text])

(defn- section [title]
  [:div.font-bold.pu-4 title])

(defn- input-box [label val]
  [:input.text-center.size-6.outline {:name label :value val}])

;; FIXME: 削るより足したら？
(defn- problem-form
  [{:keys [db/id problem/valid week num problem test gpt updated] :as params}]
  (t/log! {:level :info :id "problem-form"})
  (t/log! {:level :debug :data params})
  [:div
   [:div.text-2xl.font-bold "Problem"]
   [:form.mx-4 {:method "post"}
    (h/raw (anti-forgery-field))
    [:input {:type "hidden" :name "db/id" :value id}]
    (section "problem/valid")
    [:input (merge {:type "radio" :name "problem/valid" :value "true"}
                   (when valid {:checked "checked"})) "true "]
    [:input (merge {:type "radio" :name "problem/valid" :value "false"}
                   (when-not valid {:checked "checked"})) "false"]
    (section "week-num")
    [:div (input-box "week" week) " - " (input-box "num" num)]
    (section "problem")
    (div-textarea "problem" problem)
    (section "test")
    (div-textarea "test" test)
    (section "gpt")
    (div-textarea "gpt" gpt)
    (section "updated")
    [:div updated]
    [:br]
    [:div [:button {:class btn} "upsert"]]]])

(defn upsert! [{params :params}]
  (t/log! {:level :info :id "upsert!" :data (params "db/id")})
  (let [[id true?] (if (= "-1" (params "db/id"))
                     [-1 true]
                     [(parse-long (params "db/id")) (= "true" (params "problem/valid"))])]
    (ds/put! (-> params
                 (dissoc :__anti-forgery-token "problem/valid" "db/id")
                 (assoc :db/id id :problem/valid true? :updated (jt/local-date-time))
                 (update :week parse-long)
                 (update :num parse-long)))
    (resp/redirect "/admin/problems")))

(def get-problems
  '[:find ?e ?valid ?week ?num ?problem ?test ?gpt ?updated
    :keys e  valid  week  num  problem  test  gpt  updated
    :where
    [?e :problem/valid ?valid]
    [?e :week ?week]
    [?e :num ?num]
    [?e :problem ?problem]
    [?e :test ?test]
    [?e :gpt ?gpt]
    [?e :updated ?updated]])

(defn- div-problems []
  (t/log! :debug "div-problems")
  [:div
   [:div
    (for [p (->> (ds/qq get-problems)
                 (sort-by (juxt (fn [x] (* -1 (:week x))) :num)))]
      [:div.flex.gap-4
       [:div.flex.gap-2
        [:div [:button.text-bold.text-red-600.hover:bg-red-600.hover:text-white
               {:hx-delete (str "/admin/delete/" (:e p))}
               "D"]]
        [:div [:a.text-bold.text-sky-600.hover:bg-sky-600.hover:text-white
               {:href (str "/admin/update/" (:e p))}
               "E"]]
        [:div (:week p) "-" (:num p)]]
       [:div (:problem p)]
       [:div (:test p)]
       [:div (:gpt p)]])]])

(defn problems [request]
  (t/log! {:level :info :id "problems" :msg (user request)})
  (page
   [:div
    [:div.text-2xl.font-bold "Problems"]
    [:div.m-4
     [:a {:class btn :href "/admin/new"} "new"]
     (div-problems)]]))

(defn new [request]
  (t/log! {:lelvel :info :id (user request)})
  (page
   (problem-form {:db/id -1
                  :problem/valid "true"
                  :week ""
                  :num ""
                  :problem ""
                  :test ""
                  :gpt ""})))

(defn edit [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "edit" :data {:e e}})
  (page
   (problem-form (ds/pl (parse-long e)))))

(defn delete! [request]
  (page [:div "delete!"]))
