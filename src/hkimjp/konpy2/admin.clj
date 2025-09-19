(ns hkimjp.konpy2.admin
  (:require
   [clojure.string :as str]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page redirect]]
   [hkimjp.konpy2.util :refer [btn user now abbrev]]))

(defn- section [title]
  [:div.font-bold title])

(defn- input-box [label val]
  [:input.text-center.size-6.outline {:name label :value val}])

(defn- problem-form
  [{:keys [db/id problem/status week num problem testcode updated] :as params}]
  (t/log! {:level :info :id "problem-form"})
  (t/log! {:level :debug :data params})
  (t/log! {:level :debug :data {:id id :status status}})
  [:div.m-4
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
    [:textarea.w-full.h-20.p-2.outline.outline-black.shadow-lg
     {:name "problem"} problem]
    (section  "testcode")
    [:textarea.w-full.h-40.p-2.outline.outline-black.shadow-lg
     {:name "testcode"} testcode]
    (section  "updated")
    [:div updated]
    [:br]
    [:div [:button {:class btn} "upsert"]]
    [:br]]])

(defn upsert! [{params :params}]
  (let [{:keys [id status week num problem testcode]} params
        id (if (= -1 id) -1 (parse-long id))
        data {:db/id id
              :problem/status status
              :week (parse-long week)
              :num  (parse-long num)
              :problem problem
              :testcode testcode
              :updated (now)}]
    (t/log! {:level :debug :data data})
    (try
      (ds/put! data)
      (redirect "/admin/problems")
      (catch Exception e
        (t/log! {:level :error :msg e})))))

(def ^:private get-problems
  '[:find ?e ?status ?week ?num ?problem ?testcode ?updated
    :keys e  status  week  num  problem  testcode  updated
    :where
    [?e :problem/status ?status]
    [?e :week ?week]
    [?e :num ?num]
    [?e :problem ?problem]
    [?e :testcode ?testcode]
    [?e :updated ?updated]])

(defn- first-n [s n]
  (-> (str/split-lines s)
      first
      (abbrev n)))

(defn problems [request]
  (t/log! {:level :info :id "problems" :msg (user request)})
  (page
   [:div.m-4
    [:div.text-2xl.font-bold "Problems"]
    [:div.m-4
     [:div [:a {:class btn :href "/admin/new"} "new"]]
     [:div
      (for [p (->> (ds/qq get-problems)
                   (sort-by (juxt (fn [x] (* -1 (:week x))) :num)))]
        [:div
         [:a.hover:underline {:href (str "/admin/update/" (:e p))}
          [:div.flex.gap-4
           [:div (:week p) "-" (:num p)]
           [:div {:class "w-2/3"} (-> (:problem p)  (first-n 40))]
           [:div {:class "w-1/4"} (-> (:testcode p) (first-n 20))]]]])]]]))

(defn new [request]
  (t/log! {:lelvel :info :id (user request)})
  (page
   (problem-form {:db/id -1
                  :problem/status "yes"
                  :week ""
                  :num ""
                  :problem ""
                  :testcode ""})))

(defn edit [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "edit" :data {:e e}})
  (page
   (problem-form (ds/pl (parse-long e)))))

; (defn toggle! [{params :params}]
;   (t/log! {:level :info :id "toggle!" :data params})
;   (page [:div "toggle!"]))
