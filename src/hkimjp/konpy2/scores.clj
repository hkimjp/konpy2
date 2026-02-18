(ns hkimjp.konpy2.scores
  (:require
   [hiccup2.core :as h]
   [nextjournal.markdown :as md]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.konpy2.queries :as q]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [user btn]]))

(defn- score [sym coll target]
  [:div
   (for [[e _] coll]
     [:span.hover:underline
      {:hx-get    (str "/k/score/" e)
       :hx-target (str "#" target)
       :hx-swap   "innerHTML"}
      sym])])

; ☀️🌥️⛅️🌧️💧☂️☁️❤️💛🔴💚🩵🩶🟢🔸◾️

(defn- div-score [ABC received]
  (let [pict  {"A" "❤️", "B" "💚","C" "🩶"}]
    [:div
     [:div.flex
      [:div ABC ": "]
      (score (pict ABC) (filter #(= ABC (second %)) received) ABC)]
     [:div.mx-4 {:id ABC}]]))

(defn- week-num [e]
  (let [record (q/entries e)]
    (t/log! {:level :debug :data record})
    (try
      (if (some? (:week record))
        (str (:week record) "-" (:num record))
        (week-num (:to record)))
      (catch Exception ex
        (t/log! :error (.getMessage ex))
        nil))))

(defn hx-show
  "returns answer week-num in pre"
  [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "hx-show"})
  (let [e (parse-long e)
        submit (q/entries e)]
    (hx [:div
         [:div [:span.font-bold "problem: "] (week-num e)]
         [:div [:span.font-bold "updated: "] (:updated submit)]
         (if-let [answer (:answer submit)]
           [:pre.border-1 answer]
           [:div.border-1 (-> (:comment submit)
                              md/parse
                              md/->hiccup)])
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
    [:input.border-1.px-1.rounded {:name "user" :value "hkimura"}]
    [:button {:class btn
              :hx-post    "/k/scores/peep"
              :hx-target "#peep"
              :hx-swap   "innerHTML"} "peep"]]
   [:div#peep]])

(defn hx-peep [{{:keys [user]} :params}]
  (t/log! {:level :info :id "hx-peep" :data user})
  (let [ans  (sort (q/answers user))
        coms (sort (q/sent user))]
    (hx [:div
         "peep service was stopped"])
    #_(hx [:div
           (section ans  "💪" (str user " answered(" (count ans) ")") "peep-answer")
           (section coms "😃" (str user " comments(" (count coms) ")") "peep-sent")])))

(defn scores [request]
  (let [author   (user request)
        answered (sort (q/answers author))
        sent     (sort (q/sent author))
        received (sort (q/received author))]
    (t/log! {:level :info :msg (str "scores " author)})
    (page
     [:div.m-4
      [:div.text-2xl (format "Scores (%s)" author)]
      [:p "失った平常点は取り返せない。日頃から取り組まないと平常点がなくなる。"]
      [:p "konpy の出題は週平均6つの予定。一題解いたら3個は他の回答読んでコメントしなさい。"]
      [:br]
      (section answered "💪" (str "Your Answers(" (count answered) ")") "answered")
      (section sent "😃" (str "Comments Sent(" (count sent) ")") "sent")
      [:div.font-bold.my-4 "Comments Received"]
      [:div.mx-4
       (for [sc ["A" "B" "C"]]
         (div-score sc received))]
      (peep-section)])))
