(ns hkimjp.konpy2.scores
  (:require
   [clojure.string :as str]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [user]]))

(def ^:private answered
  '[:find ?e
    :in $ ?author
    :where
    [?e :answer/status "yes"]
    [?e :author ?author]])

(def ^:private sent
  '[:find ?e
    :in $ ?author
    :where
    [?e :comment/status "yes"]
    [?e :author ?author]])

(def ^:private received
  '[:find ?e ?pt
    :in $ ?author
    :where
    [?e :comment/status "yes"]
    [?e :to ?a]
    [?a :author ?author]
    [?e :pt ?pt]])

(defn- score [sym coll target]
  [:div
   (for [[e _] coll]
     [:span.hover:underline
      {:hx-get    (str "/k/score/" e)
       :hx-target (str "#" target)
       :hx-swap   "innerHTML"} sym])])

(def ^:private pict {"A" "❤️", "B" "💚","C" "🩶"})

; ☀️🌥️⛅️🌧️💧☂️☁️❤️💛🔴💚🩵🩶🟢🔸◾️

(defn- div-score [ABC received]
  [:div
   [:div.flex
    [:div ABC ": "]
    (score (pict ABC) (filter #(= ABC (second %)) received) ABC)]
   [:div {:id ABC}]])

(defn hx-show [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "hx-show"})
  (let [submit (ds/pl (parse-long e))]
    (hx [:div
         [:div [:span.font-bold "updated: "] (:updated submit)]
         [:pre.border-1 (or (:comment submit) (:answer submit))]
         [:br]])))

(defn scores [request]
  (let [author   (user request)
        answered (sort (ds/qq answered author))
        sent     (sort (ds/qq sent author))
        received (sort (ds/qq received author))]
    (t/log! {:level :info :msg (str "scores " author)})
    (page
     [:div.m-4
      [:div.text-2xl (format "Scores (%s)" author)]
      [:p "平常点は平常につく。日頃から取り組まないと平常点がなくなる。失った平常点は取り返せない。"]
      [:p "konpy の出題は週平均6つの予定。一題解いたら3個は他の回答読んでコメントしなさい。"]
      [:p "平常点はコメント重視。"]
      [:br]
      [:div.font-bold "Your Answers"]
      [:div.mx-4 (score "💪" answered "answered")]
      [:div.font-bold.my-4 "Comments Sent"]
      [:div.mx-4 (score "😃" sent "sent")]
      [:div.font-bold.my-4 "Comments Received"]
      [:div.mx-4
       (for [sc ["A" "B" "C"]]
         (div-score sc received))]])))

