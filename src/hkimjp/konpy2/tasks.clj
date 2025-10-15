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
   [hkimjp.konpy2.util :refer [btn input-box user week now local-date]]))

(defn- wk [] (max 0 (week)))

(defn- hx-component [url target title]
  [:div
   [:div.hover:underline {:hx-get url
                          :hx-target (str "#" target)
                          :hx-swap "innerHTML"}
    [:span.font-bold title]]
   [:div {:id target}]])

(defn konpy [request]
  (t/log! {:level :info :msg (str "tasks/konpy " (user request))})
  (let [fetch-problems '[:find ?e ?week ?num ?problem
                         :keys e  week num  problem
                         :in $ ?week
                         :where
                         [?e :problem/status "yes"]
                         [?e :week ?week]
                         [?e :num ?num]
                         [?e :problem ?problem]]]
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
      [:div.text-2xl "本日の回答・コメント・ログイン"]
      [:div.m-4.flex.gap-4
       (hx-component "/k/hx-answers" "answers" "回答")
       (hx-component "/k/hx-comments" "comments" "コメント")
       (hx-component "/k/hx-logins" "logins" "ログイン")]])))

(defn- answerers [pid author]
  (t/log! {:level :debug :id "answerers" :msg (str "pid " pid)})
  (let [fetch-answers '[:find ?e ?author
                        :in $ ?id
                        :where
                        [?e :answer/status "yes"]
                        [?e :to ?id]
                        [?e :author ?author]]]
    [:div
     [:div.font-bold "answers"]
     (into [:div.inline.my-4]
           (for [[eid user] (ds/qq fetch-answers pid)]
             [:button.pr-4
              {:hx-get (str "/k/answer/" eid "/" pid)
               :hx-target "#answer"
               :hx-swap "innerHTML"}
              [:span.hover:underline (if (= user author) user "******")]]))
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
      [:div.text-2xl (format "Problem %d-%d, %s, answers: %s comments: %s"
                             (:week p) (:num p) local-date answers comments)]
      [:div.m-4
       [:p (:problem p)]
       (answerers eid author)
       [:div.font-bold "your answer"]
       [:form {:method "post"
               :action "/k/answer"
               :enctype "multipart/form-data"}
        (h/raw (anti-forgery-field))
        [:input {:type "hidden" :name "e" :value eid}]
        [:input {:class input-box :type "file" :accept ".py" :name "file"}]
        [:button {:class btn} "upload"]]]])))

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
            (let [updated (subs (str updated) 11 19)]
              [:li.font-mono
               (format "%d-%d %s %s" week num updated author)]))]])))

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
         (let [updated (subs (str updated) 11 19)]
           [:li.font-mono
            (format "%d-%d %s %s → %s" week num updated author commentee)]))]])))

; (defn hx-stocks [request]
;   (let [stocks
;         '[:find ?e ?owner ?updated
;           :keys e  owner  updated
;           :in $ ?now
;           :where
;           [?e :stock/status "yes"]
;           [?e :owner ?owner]
;           [?e :updated ?updated]
;           [(java-time.api/before? ?now ?updated)]] owner (user request)
;         stocks (ds/qq stocks (jt/adjust (now) (jt/local-time 0)))]
;     (t/log! {:level :info :id "hx-stocks" :data {:owner owner :stocks stocks}})
;     (hx [:div
;          [:div (format "(%d)" (count stocks))]
;          [:p "ストックは個人的なもの。"
;           "何個ストックされた以外の表示をやめる。"]
;          ; [:ul.list-disc.mx-4
;          ;    (for [{:keys [updated owner]} (-> (sort-by :e stocks) reverse)]
;          ;      [:li.font-mono (jt/format "HH:mm:ss " updated) owner])]
;          ])))

(defn hx-logins [request]
  (let [user (user request)
        today (local-date)
        logins (->> (slurp "log/konpy.log")
                    (str/split-lines)
                    (filter  #(str/starts-with? % today))
                    (filter #(re-find #"success" %))
                    (map #(str/split % #"\s+"))
                    (map last)
                    reverse)]
    (t/log! {:level :debug :id "hx-logins" :msg user})
    (hx [:div "(昨日からのログイン継続を除く。)"
         [:ul.list-disc.mx-4
          (for [login logins]
            [:li.font-mono login])]])))

