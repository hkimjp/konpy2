(ns hkimjp.konpy2.scores
  (:require
   [clojure.string :as str]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page]]
   [hkimjp.konpy2.util :refer [user]]))

(def ^:private answered
  '[:find ?e
    :in $ ?author
    :where
    [?e :answer/status "yes"]
    [?e :author ?author]])

; (ds/qq answered "hkimura")

(def ^:private sent
  '[:find ?e
    :in $ ?author
    :where
    [?e :comment/status "yes"]
    [?e :author ?author]])

; (ds/qq sent "hkimura")

(def ^:private received
  '[:find ?e ?pt
    :in $ ?author
    :where
    [?e :comment/status "yes"]
    [?e :to ?a]
    [?a :author ?author]
    [?e :pt ?pt]])

; (ds/qq received "tue2")
; (filter #(= "B" (second %)) (ds/qq received "hkimura"))
; (ds/qq received "chatgpt")

(defn- score [sym coll]
  (str/join (repeat (count coll) sym)))

; (score "😃" (ds/qq sent "hkimura"))

(def ^:private pict {"A" "❤️", "B" "💚","C" "🩶"})

; ☀️🌥️⛅️🌧️💧☂️☁️❤️💛🔴💚🩵🩶🟢🔸◾️

(defn- div-score [ABC received]
  [:div ABC ": " (score (pict ABC) (filter #(= ABC (second %)) received))])

; (div-score "A" (ds/qq received "hkimura"))

(defn scores [request]
  (let [author (user request)
        answered (ds/qq answered author)
        sent (ds/qq sent author)
        received (ds/qq received author)]
    (t/log! {:level :info :msg (str "scores " author)})
    (page
     [:div.m-4
      [:div.text-2xl "Scores " author]
      [:p "平常点は平常につく。日頃から取り組まないと平常点がなくなる。失った平常点は取り返せない。"]
      [:p "konpy の出題は週平均6つの予定。一題解いたら3個は他の回答読んでコメントしなさい。"]
      [:p "平常点はコメント重視。"]
      [:br]
      [:div.font-bold "Answered"]
      [:div.mx-4 (score "💪" answered)]
      [:div.font-bold "Comments Sent"]
      [:div.mx-4 (score "😃" sent)]
      [:div.font-bold "Comments Received"]
      [:div.mx-4
       (for [sc ["A" "B" "C"]]
         (div-score sc received))]])))

