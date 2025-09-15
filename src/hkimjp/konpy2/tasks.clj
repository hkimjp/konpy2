(ns hkimjp.konpy2.tasks
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [btn input-box user week now]]))

(def q '[:find ?e ?num ?problem
         :keys e  num  problem
         :in $ ?week
         :where
         [?e :problem/valid true]
         [?e :week ?week]
         [?e :num ?num]
         [?e :problem ?problem]])

(defn- wk [] (max 0 (week)))

(defn konpy [request]
  (t/log! {:level :info :id "list" :data (user request)})
  (page
   [:div
    [:div.text-2xl "今週の Python"]
    (into [:div.m-4]
          (for [{:keys [e num problem]} (->> (ds/qq q (wk))
                                             (sort-by :num))]
            [:div.flex.gap-4 [:a {:href (str "/k/problem/" e)} num]  [:div problem]]))]))

;------------------------------

(def answers '[:find ?e ?author
               :in $ ?id
               :where
               [?e :answer/valid true]
               [?e :to ?id]
               [?e :author ?author]])

(defn- div-answerers [e]
  (t/log! {:level :info :id "div-answerers" :data e})
  [:div
   [:div.font-bold "answers"]
   (into [:div.inline.my-4]
         (for [[eid user] (ds/qq answers e)]
           [:button.pr-4
            {:hx-get (str "/k/answer/" eid)
             :hx-target "#answer"
             :hx-swap "innerHTML"}
            [:span.hover:underline user]]))
   [:div#answer "[answer]"]])

(defn show-answer [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "show-answer" :data e})
  (let [ans (ds/pl (parse-long e))]
    (hx [:div
         [:div [:span.font-bold "author: "] (:author ans)]
         [:div [:span.font-bold "updated: "] (:updated ans)]
         [:pre.border-1.p-2 (:answer ans)]
         [:div.font-bold "your comment"]
         [:form {:method "post" :action "/k/comment"}]])))

(defn post-answer [{{:keys [file e]} :params :as request}]
  (t/log! {:level :info :id "post-answer"})
  (t/log! {:level :debug :data {:e e :file file}})
  (try
    (ds/put! {:answer/valid true
              :to     (parse-long e)
              :author (user request)
              :answer (slurp (:tempfile file))
              :digest 0
              :updated (now)})
    (resp/redirect (str "/k/problem/" e))
    (catch Exception e
      (t/log! {:level :error :data file})
      (page
       [:div
        [:div.text-2xl.text-red-600 "Error"]
        [:p (.getMessage e)]]))))

(defn problem [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "problem" :data e})
  (let [e (parse-long e)
        p (ds/pl e)]
    (page
     [:div
      [:div.text-2xl (str "Problem " (:week p) "-" (:num p))]
      [:div.m-4
       [:p (:problem p)]
       (div-answerers e)
       [:div.font-bold "your answer"]
       [:form {:method "post"
               :action "/k/answer"
               :enctype "multipart/form-data"}
        (h/raw (anti-forgery-field))
        [:input {:type "hidden" :name "e" :value e}]
        [:input {:class input-box :type "file" :accept ".py" :name "file"}]
        [:button {:class btn} "upload"]]]])))

;------------------------------------

(defn post-comment [{params :params}]
  (page [:div "under construction"]))
