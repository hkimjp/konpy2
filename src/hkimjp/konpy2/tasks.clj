(ns hkimjp.konpy2.tasks
  (:require
   [hiccup2.core :as h]
   [java-time.api :as jt]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
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

(defn konpy [request]
  (t/log! {:level :info :msg (str "tasks/konpy " (user request))})
  (page
   [:div.m-4
    [:div.text-2xl "今週の Python"]
    (into [:div.m-4]
          (for [{:keys [e week num problem]} (->> (ds/qq fetch-problems (wk))
                                                  (sort-by :num))]
            [:div
             [:a.hover:underline
              {:href (str "/k/problem/" e)}
              [:span.mr-4 week "-" num] [:span problem]]]))
    [:div.text-2xl "本日の Konpy"]
    [:p "under construction, should be mixed."]
    [:div.hover:underline {:hx-get    "/k/hx-answers"
                           :hx-target "#answers"
                           :hx-swap   "innerHTML"} "回答"]
    [:div#answers "[***]"]
    [:div.hover:underline {:hx-get    "/k/hx-comments"
                           :hx-target "#comments"
                           :hx-swap   "innerHTML"} "コメント"]
    [:div#comments "[***]"]]))

(def ^:private fetch-answers '[:find ?e ?author
                               :in $ ?id
                               :where
                               [?e :answer/status "yes"]
                               [?e :to ?id]
                               [?e :author ?author]])

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

; (defn post-comment [{params :params}]
;   (t/log! {:level :info :id "post-comment" :data params})
;   (page [:div "under construction"]))

(defn- todays-answers
  "answers after `date-time`"
  ([] (todays-answers (jt/local-date-time)))
  ([date-time]
   (ds/qq '[:find ?e ?author ?week ?num
            :keys e  author  week  num
            :in $ ?now
            :where
            [?e :answer/status "yes"]
            [?e :updated ?updated]
            [(jt/before? ?now ?updated)]
            [?e :to ?to]
            [?e :author ?author]
            [?to :week ?week]
            [?to :num ?num]]
          (jt/adjust date-time (jt/local-time 0)))))

(defn hx-answers [request]
  (t/log! {:level :info :id "hx-answers"})
  (let [answers (todays-answers)]
    (t/log! :debug (str todays-answers))
    (hx [:div "answers3"
         [:p (str answers)]])))

(comment
  (hx-answers nil)
  (todays-answers)
  (sort-by :e (todays-answers))
  (for [{:keys [author week num updated]} (sort-by :e (todays-answers))]
    [:li (format "%d-%d %s %s" week num author updated)])
  :rcf)

(defn- todays-comments
  "comments after `date-time`"
  ([] (todays-comments (jt/local-date-time)))
  ([date-time]
   (ds/qq '[:find ?e ?author ?week ?num ?updated
            :keys e  author  week  num  updated
            :in $ ?now
            :where
            [?e :comment/status "yes"]
            [?e :updated ?updated]
            [(jt/before? ?now ?updated)]
            [?e :to ?to]
            [?e :author ?author]
            [?to :to ?p]
            [?p :week ?week]
            [?p :num ?num]]
          (jt/adjust date-time (jt/local-time 0)))))

; (todays-comments (jt/local-date-time 2025 9 18))

(defn hx-comments [request]
  (t/log! {:level :info :id "hx-comments"})
  (hx [:div "comments"]))


