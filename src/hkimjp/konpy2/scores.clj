(ns hkimjp.konpy2.scores
  (:require
   [clojure.string :as str]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [user btn]]))

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

; ☀️🌥️⛅️🌧️💧☂️☁️❤️💛🔴💚🩵🩶🟢🔸◾️

(defn- div-score [ABC received]
  (let [pict  {"A" "❤️", "B" "💚","C" "🩶"}]
    [:div
     [:div.flex
      [:div ABC ": "]
      (score (pict ABC) (filter #(= ABC (second %)) received) ABC)]
     [:div.mx-4 {:id ABC}]]))

(defn- week-num [e]
  (let [record (ds/pl e)]
    (t/log! {:level :debug :data record})
    (try
      (if (some? (:week record))
        (str (:week record) "-" (:num record))
        (week-num (:to record)))
      (catch Exception ex
        (t/log! :error (.getMessage ex))
        nil))))

(defn hx-show [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "hx-show"})
  (let [e (parse-long e)
        submit (ds/pl e)]
    (hx [:div
         [:div [:span.font-bold "problem: "] (week-num e)]
         [:div [:span.font-bold "updated: "] (:updated submit)]
         [:pre.border-1 (or (:comment submit) (:answer submit))]
         [:br]])))

(defn- section [thing sym label target]
  [:div
   [:div.font-bold label]
   [:div.mx-4 (score sym thing target)]
   [:div.mx-4 {:id target}]])

(defn- peep-section []
  [:div
   [:div.font-bold.my-4 "Peep other student's score"]
   [:form
    (h/raw (anti-forgery-field))
    [:input.border-1.p-2 {:name "user"}]
    [:buttn {:class btn
             :hx-post    "/k/scores/peep"
             :hx-target "#peep"
             :hx-swap   "innerHTML"} "peep"]]
   [:div#peep]])

(defn hx-peep [{params :params}]
  (t/log! {:level :info :id "hx-peep" :data params})
  (hx [:div (str "data: " params)]))

(defn scores [request]
  (let [author   (user request)
        answered (sort (ds/qq answered author))
        sent     (sort (ds/qq sent author))
        received (sort (ds/qq received author))]
    (t/log! {:level :info :msg (str "scores " author)})
    (page
     [:div.m-4
      [:div.text-2xl (format "Scores (%s)" author)]
      [:p "失った平常点は取り返せない。日頃から取り組まないと平常点がなくなる。"]
      [:p "konpy の出題は週平均6つの予定。一題解いたら3個は他の回答読んでコメントしなさい。"]
      [:br]
      (section answered "💪" "Your Answers" "answered")
      (section sent "😃" "Comments Sent" "sent")
      [:div.font-bold.my-4 "Comments Received"]
      [:div.mx-4
       (for [sc ["A" "B" "C"]]
         (div-score sc received))]
      ; peep section, 0.3.13
      (peep-section)])))

