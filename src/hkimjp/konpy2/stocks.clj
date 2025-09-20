(ns hkimjp.konpy2.stocks
  (:require
   [java-time.api :as jt]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [user btn now]]))

(def ^:private fetch-stocks
  '[:find ?e ?stock ?updated
    :keys e  stock  updated
    :in $ ?owner
    :where
    [?e :stock/status "yes"]
    [?e :owner ?owener]
    [?e :stock ?stock]
    [?e :updated ?updated]])

; (def s (->> (ds/qq fetch-stocks "hkimura")
;             (sort-by :e)
;             reverse
;             first))

(defn- abbrev [date-time text]
  (str
   (jt/format "yyyy-MM-dd hh:mm " (jt/local-date-time))
   (re-find #".*" text)))

(defn stocks [request]
  (let [author (user request)]
    (t/log! {:level :info :id "stocks" :msg author})
    (page [:div.m-4
           [:div.text-2xl "Stocks"]
           [:p "イージーに、コピペすると日付打ってデータベースに入れるだけとか。自分専用ページ。"]
           [:p "自分でやるべきことまでアプリ側で用意することないか。"]
           [:p "hkimura 的には時々閲覧させてビビらせる閻魔帳のイメージ。"]
           [:div.font-bold "New Stock"]
           [:form {:method "post"}
            (h/raw (anti-forgery-field))
            [:textarea.border-1.w-120.h-40.p-2 {:name "text"}]
            [:button {:class btn
                      :hx-post   "/k/stocks"
                      :hx-target "#stocks"
                      :hx-swap   "afterbegin"}
             "stock"]]
           [:div.font-bold "Your Stocks"]
           [:div.flex
            [:div#stocks {:class "w-1/2"}
             (for [s (->> (ds/qq fetch-stocks author)
                          (sort-by :e)
                          reverse)]
               [:p [:a.hover:underline
                    {:hx-get    (str "/k/stock/" (:e s))
                     :hx-target "#preview"
                     :hx-swap   "innerHTML"}
                    (abbrev (:updated s) (:stock s))]])]
            [:div#preview {:class "w-1/2 border-1"}]]])))

(defn stocks! [{{:keys [text]} :params :as request}]
  (let [owner (user request)]
    (t/log! {:level :info :id "stocks!" :data {:text text} :msg owner})
    (ds/put! {:stock/status "yes"
              :owner owner
              :stock text
              :updated (now)})
    ;; trick
    (hx [:p (abbrev (jt/local-date-time) text)])))

(defn stock [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "stock" :msg e})
  (hx [:pre (:stock (ds/pl (parse-long e)))]))
