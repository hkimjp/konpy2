(ns hkimjp.konpy2.scores
  (:require
   [taoensso.telemere :as t]
   [hkimjp.konpy2.response :refer [page]]
   [hkimjp.konpy2.util :refer [user]]))

(defn scores [request]
  (t/log! {:level :info :msg (str "scores " (user request))})
  (page
   [:div.m-4
    [:div.text-2xl "Scores"]
    [:p "日頃から取り組まないと平常点がなくなる。失った平常点は取り返せない。平常点は平常につく。"]
    [:p "情報処理応用では、中間、期末テストは 5 問中 2 問できないと C はつけない。"
     "前期の情報基礎では 0.5 問で C つけた。しかも C は 20/30 点だった。"
     "hkimura の誤りだった。勘違いを増やした。是正する。"]]))

