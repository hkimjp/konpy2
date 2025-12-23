(ns hkimjp.konpy2.tasks
  (:require
   [clojure.string :as str]
   [hiccup2.core :as h]
   [java-time.api :as jt]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer
    [btn input-box user week now local-date HH:mm]]))

(defn- wk
  "今週は授業開始後、第何週か？"
  [] (max 0 (week)))

(defn- hx-component [url target title]
  [:div
   [:button {:class btn
             :hx-get url
             :hx-target (str "#" target)
             :hx-swap "innerHTML"}
    [:span.font-bold title]]
   [:div {:id target}]])

; ranamed
(defn tasks [request]
  (t/log! {:level :info :msg (str "tasks/tasks " (user request))})
  (let [fetch-problems '[:find ?e ?week ?num ?problem
                         :keys e  week num  problem
                         :in $ ?week
                         :where
                         [?e :week ?week]
                         [?e :num ?num]
                         [?e :problem ?problem]
                         [?e :problem/status "yes"]]]
    (page
     [:div.m-4
      [:div.text-2xl (format "今週の Python (%s)" (user request))]
      (into [:div.m-4]
            (for [{:keys [e week num problem]} (->> (ds/qq fetch-problems (wk))
                                                    (sort-by :num))]
              [:div
               [:a.hover:underline
                {:href (str "/k/problem/" e)}
                [:span.mr-4 week "-" num] [:span problem]]]))
      [:div.m-4.flex.gap-4
       (hx-component "/k/hx-answers" "answers" "本日の回答")
       (hx-component "/k/hx-comments" "comments" "コメント")
       (hx-component "/k/hx-logins" "logins" "ログイン")]])))

;; winter vacation
(def fetch-problems-all
  '[:find ?e ?week ?num ?problem
    :keys e  week  num  problem
    :where
    [?e :problem/status "yes"]
    [?e :week ?week]
    [?e :num ?num]
    [?e :problem ?problem]])

(def answered-q
  '[:find [?pid ...]
    :in $ ?author
    :where
    [?e :author ?author]
    [?e :answer/status "yes"]
    [?e :to ?pid]])

(comment
  (->> (ds/qq fetch-problems-all)
       (sort-by (juxt :week :num)))
  (ds/qq answered-q "tue2")
  (let [author "tue2"]
    (ds/qq answered-q  author))
  :rcf)

(defn tasks-all [request]
  (let [author   (user request)
        answered (set (ds/qq answered-q author))]
    (t/log! {:level :debug
             :id "tasks-all"
             :data {:author author :answered answered}})
    (page
     [:div.m-4
      [:div "winter special: problems all"]
      (into [:div.m-4]
            (for [{:keys [e week num problem]} (->> (ds/qq fetch-problems-all)
                                                    (sort-by (juxt :week :num)))]
              [:div
               (if (answered e)
                 "⭕️ "
                 "✖️ ")
               [:a.hover:underline
                {:href (str "/k/problem/" e)}
                [:span.mr-4 week "-" num] [:span problem]]]))])))

;;
(defn- answerers [pid author]
  (t/log! {:level :debug :id "answerers" :msg (str "pid " pid)})
  (let [fetch-answers '[:find ?e ?author
                        :in $ ?id
                        :where
                        [?e :to ?id]
                        [?e :author ?author]
                        [?e :answer/status "yes"]]]
    [:div
     [:div.font-bold "answers"]
     (into [:div.inline.my-4]
           (for [[eid user] (->> (ds/qq fetch-answers pid)
                                 (sort-by first)
                                 #_reverse)]
             [:button.pr-4
              {:hx-get (str "/k/answer/" eid "/" pid)
               :hx-target "#answer"
               :hx-swap "innerHTML"}
              [:span.hover:underline
               (if (= user author)
                 [:span.text-red-600 user]
                 user)]]))
     [:div#answer "[answer]"]]))

(defn problem [{{:keys [e]} :path-params :as request}]
  (let [eid (parse-long e)
        p (ds/pl eid)
        author (user request)
        local-date (local-date)
        answers (c/llen (format "kp2:%s:uploads:%s" author local-date))
        comments (c/llen (format "kp2:%s:comments:%s" author local-date))]
    (t/log! {:level :info :id "problem" :msg author})
    (t/log! {:level :debug :data {:answers answers :comments comments}})
    (page
     [:div.m-4
      [:div.text-2xl (format "Problem %d-%d, %s, answers: %s, comments: %s"
                             (:week p) (:num p) local-date answers comments)]
      [:div.m-4
       [:p (:problem p)]
       (answerers eid author)
       [:div#answer
        [:div.font-bold "your answer"]
        [:form {:method "post"
                :action "/k/answer"
                :enctype "multipart/form-data"}
         (h/raw (anti-forgery-field))
         [:input {:type "hidden" :name "e" :value eid}]
         [:input {:class input-box :type "file" :accept ".py" :name "file"}]
         [:button {:class btn} "upload"]]]]])))

(defn todays-answers
  ([] (todays-answers (now)))
  ([date-time] (ds/qq '[:find ?e ?author ?week ?num ?updated
                        :keys e  author  week  num  updated
                        :in $ ?now
                        :where
                        [?e :answer/status "yes"]
                        [?e :updated ?updated]
                        [(java-time.api/before? ?now ?updated)]
                        [?e :to ?to]
                        [?e :author ?author]
                        [?to :week ?week]
                        [?to :num ?num]]
                      (jt/adjust date-time (jt/local-time 0)))))

(defn- color-author [author user]
  (if (= author user)
    [:span.text-red-500 author]
    author))

(defn hx-answers [request]
  (let [user (user request)
        answers (todays-answers)]
    (t/log! {:level :info :id "hx-answers" :msg user})
    (hx [:div
         [:div (format "(%d)" (count answers))]
         [:ul.list-disc.mx-4
          (for [{:keys [week num author updated]}
                (->> answers
                     (sort-by :e)
                     reverse)]
            (let [updated (HH:mm updated)]
              [:li.font-mono
               week "-" num " " updated " " (color-author author user)]))]])))

(defn- todays-comments
  "comments after `date-time`"
  ([] (todays-comments (now)))
  ([date-time]
   (ds/qq '[:find ?e ?author ?week ?num ?updated ?commentee
            :keys e  author  week  num  updated  commentee
            :in $ ?now
            :where
            [?e :comment/status "yes"]
            [?e :updated ?updated]
            [(java-time.api/before? ?now ?updated)]
            [?e :to ?to]
            [?e :author ?author]
            [?to :author ?commentee]
            [?to :to ?p]
            [?p :week ?week]
            [?p :num ?num]]
          (jt/adjust date-time (jt/local-time 0)))))

(defn hx-comments [request]
  (let [user (user request)
        comments (todays-comments)]
    (t/log! {:level :info :id "hx-comments" :msg user})
    (hx
     [:div
      [:div (format "(%d)" (count comments))]
      [:ul.list-disc.mx-4
       (for [{:keys [week num author updated commentee]}
             (->> comments
                  (sort-by :e)
                  reverse)]
         (let [updated (HH:mm updated)]
           [:li.font-mono
            week "-" num " " updated " "
            (color-author author user) " -> " (color-author commentee user)]))]])))

(defn hx-logins [request]
  (let [user (user request)
        logins (c/lrange (format "kp2:login:%s" (local-date)))]
    (hx [:div (format "(%d)" (count logins))
         [:ul.list-disc.mx-4
          (for [login logins]
            (let [[login time] (str/split login #"\s")]
              [:li.font-mono time " " (color-author login user)]))]])))
