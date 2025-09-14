(ns hkimjp.konpy2.tasks
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page]]
   [hkimjp.konpy2.util :refer [btn input-box user week]]))

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

(def ans '[:find ?e ?user
           :in $ ?id
           :where
           [?e :answer/valid true]
           [?e :to ?div]
           [?e :user ?user]])

;; (ds/qq ans 1)

(defn- div-answerers [e]
  (t/log! {:level :info :id "div-answerers" :data e})
  [:div
   [:div.font-bold "answers"]
   (into [:div.my-4.gap-2]
         (for [[eid user] (ds/qq ans e)]
           [:a {:href (str "/k/answer/" eid)} user]))])

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
       [:form {:method "post"
               :action "/k/answer"
               :enctype "multipart/form-data"}
        (h/raw (anti-forgery-field))
        [:input {:type "hidden" :name "e" :value e}]
        [:input {:class input-box :type "file" :accept ".py" :name "file"}]
        [:button {:class btn} "upload"]]]])))
