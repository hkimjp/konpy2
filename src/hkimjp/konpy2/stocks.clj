(ns hkimjp.konpy2.stocks
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [user btn]]))

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
            [:textarea.border-1.w-120.h-40.p-2 {:name "stock"}]
            [:button {:class btn
                      :hx-post   "/k/stocks"
                      :hx-target "#stocks"
                      :hx-swap   "innerHTML"}
             "stock"]]
           [:div.font-bold "Your Stocks"]
           [:div#stocks]])))

(defn stocks! [{{:keys [stock]} :params :as request}]
  (let [author (user request)]
    (t/log! {:level :info :id "stocks" :data {:stock stock} :msg author})
    (hx [:div "stocks"])))
