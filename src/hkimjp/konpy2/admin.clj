(ns hkimjp.konpy2.admin
  (:require
   [java-time.api :as jt]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page]]
   [hkimjp.konpy2.util :refer [btn user now]]))

(defn admin [_request]
  (page
   [:div
    [:div.text-2xl.font-bold "admin only"]
    [:ul
     [:li [:a {:href "/admin/problems"} "problems"]]
     [:li [:a {:href "/admin/new"} "new"]]
     [:li [:a {:href "/admin/update/0"} "edit"]]
     [:li [:a {:href "/admin/delete/0"} "delete!"]]]]))

(defn- textarea [label text]
  [:textarea.w-full.h-20.p-2.outline.outline-black.shadow-lg
   {:name label} text])

(defn- section [title]
  [:div.font-bold.pu-4 title])

(defn- input-box [label val]
  [:input.text-center.size-6.outline {:name label :value val}])

(defn- problem-form
  [{:keys [db/id problem/status week num problem test updated] :as params}]
  (t/log! {:level :info :id "problem-form"})
  (t/log! {:level :debug :data params})
  (t/log! {:level :debug :data {:id id :status status}})
  [:div
   [:div.text-2xl.font-bold "Problem"]
   [:form.mx-4 {:method "post"}
    (h/raw (anti-forgery-field))
    [:input {:type "hidden" :name "id" :value id}]
    (section "problem/status")
    [:input (merge {:type "radio" :name "status" :value "yes"}
                   (when (= status "yes") {:checked "checked"})) "yes "]
    [:input (merge {:type "radio" :name "status" :value "no"}
                   (when (= status "no")  {:checked "checked"})) "no "]
    (section "week-num")
    [:div (input-box "week" week) " - " (input-box "num" num)]
    (section  "problem")
    (textarea "problem" problem)
    (section  "test")
    (textarea "test" test)
    (section  "updated")
    [:div updated]
    [:br]
    [:div [:button {:class btn} "upsert"]]
    [:br]]])

(defn upsert! [{params :params}]
  (let [{:keys [id status week num problem test]} params
        id (if (= -1 id) -1 (parse-long id))
        data {:db/id id
              :problem/status status
              :week (parse-long week)
              :num  (parse-long num)
              :problem problem
              :test test
              :updated (now)}]
    (t/log! {:level :debug :data data})
    (try
      (ds/put! data)
      (resp/redirect "/admin/problems")
      (catch Exception e
        (t/log! {:level :error :msg e})))))

(def ^:private get-problems
  '[:find ?e ?status ?week ?num ?problem ?test ?updated
    :keys e  status  week  num  problem  test  updated
    :where
    [?e :problem/status ?status]
    [?e :week ?week]
    [?e :num ?num]
    [?e :problem ?problem]
    [?e :test ?test]
    [?e :updated ?updated]])

(defn- div-problems []
  (t/log! :debug "div-problems")
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
      [:div (:test p)]])])

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
                  :problem/status "yes"
                  :week ""
                  :num ""
                  :problem ""
                  :test ""})))

(defn edit [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "edit" :data {:e e}})
  (page
   (problem-form (ds/pl (parse-long e)))))

(defn toggle! [{params :params}]
  (t/log! {:level :info :id "toggle!" :data params})
  (page [:div "toggle!"]))
