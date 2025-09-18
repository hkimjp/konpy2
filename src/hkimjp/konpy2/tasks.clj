(ns hkimjp.konpy2.tasks
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page]]
   [hkimjp.konpy2.util :refer [btn input-box user week]]))

(defn- wk [] (max 0 (week)))

(def ^:private fetch-problems '[:find ?e ?week ?num ?problem
                                :keys e  week num  problem
                                :in $ ?week
                                :where
                                [?e :problem/status "yes"]
                                [?e :week ?week]
                                [?e :num ?num]
                                [?e :problem ?problem]])

(ds/qq fetch-problems 0)

(defn konpy [request]
  (t/log! {:level :info :msg (str "tasks/konpy " (user request))})
  (page
   [:div
    [:div.text-2xl "今週の Python"]
    (into [:div.m-4]
          (for [{:keys [e week num problem]} (->> (ds/qq fetch-problems (wk))
                                                  (sort-by :num))]
            [:div
             [:a.hover:underline
              {:href (str "/k/problem/" e)}
              [:span.mr-4 week "-" num] [:span problem]]]))]))

(def ^:private fetch-answers '[:find ?e ?author
                               :in $ ?id
                               :where
                               [?e :answer/status "yes"]
                               [?e :to ?id]
                               [?e :author ?author]])

; (ds/qq fetch-answers 10)

(defn- answerers [pid]
  (t/log! {:level :info :id "answerers" :data pid})
  [:div
   [:div.font-bold "answers"]
   (into [:div.inline.my-4]
         (for [[eid user] (ds/qq fetch-answers pid)]
           [:button.pr-4
            {:hx-get (str "/k/answer/" eid "/" pid)
             :hx-target "#answer"
             :hx-swap "innerHTML"}
            [:span.hover:underline user]]))
   [:div#answer "[answer]"]])

(defn problem [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "problem" :data e})
  (let [eid (parse-long e)
        p (ds/pl eid)]
    (page
     [:div
      [:div.text-2xl (str "Problem " (:week p) "-" (:num p))]
      [:div.m-4
       [:p (:problem p)]
       (answerers eid)
       [:div.font-bold "your answer"]
       [:form {:method "post"
               :action "/k/answer"
               :enctype "multipart/form-data"}
        (h/raw (anti-forgery-field))
        [:input {:type "hidden" :name "e" :value eid}]
        [:input {:class input-box :type "file" :accept ".py" :name "file"}]
        [:button {:class btn} "upload"]]]])))

(defn post-comment [{params :params}]
  (t/log! {:level :info :id "post-comment" :data params})
  (page [:div "under construction"]))
