(ns hkimjp.konpy2.stocks
  (:require
   [clojure.string :as str]
   [hiccup2.core :as h]
   [java-time.api :as jt]
   [nextjournal.markdown :as md]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [user btn now abbrev]]))

(def ^:private fetch-stocks
  '[:find ?e ?stock ?updated
    :keys e  stock  updated
    :in $ ?owner
    :where
    [?e :stock/status "yes"]
    [?e :owner ?owner]
    [?e :stock ?stock]
    [?e :updated ?updated]])

(defn- summary [date-time text]
  (str
   (jt/format "MM/dd HH:mm " date-time)
   (-> (re-find #".*" text)
       (abbrev 20))))

(defn stocks [request]
  (let [author (user request)]
    (t/log! {:level :info :id "stocks" :msg author})
    (page [:div.m-4
           [:div.text-2xl (format "Stocks (%s)" author)]
           [:p "コピペすると日付打ってデータベースに入れるだけの自分専用ページ。"]
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
            [:div#stocks {:class "w-2/5"}
             (for [s (->> (ds/qq fetch-stocks author)
                          (sort-by :e)
                          reverse)]
               [:p [:a.hover:underline
                    {:hx-get    (str "/k/stock/" (:e s))
                     :hx-target "#preview"
                     :hx-swap   "innerHTML"}
                    (summary (:updated s) (:stock s))]])]
            [:div#preview {:class "w-3/5 border-1 text-sm"}]]])))

(defn stocks! [{{:keys [text]} :params :as request}]
  (let [owner (user request)]
    (t/log! {:level :info :id "stocks!" :data {:text text} :msg owner})
    (ds/put! {:stock/status "yes"
              :owner owner
              :stock text
              :updated (now)})
    ;; trick
    (hx [:p (summary (jt/local-date-time) text)])))

(defn- markdown? [s]
  (let [lines (str/split-lines s)]
    (or
     (< 0 (count (filter #(re-find #"^#+\s" %)  lines)))
     (< 0 (count (filter #(re-matches #"\s*" %) lines)))
     (< 0 (count (filter #(re-find #"^*+\s" %)  lines)))
     (< 0 (count (filter #(re-find #"^\d+\s" %) lines))))))

;; adaptive markdown
(defn stock [{{:keys [e]} :path-params}]
  (let [doc (:stock (ds/pl (parse-long e)))]
    (t/log! {:level :info :id "stock" :data {:e e :doc doc}})
    (if (markdown? doc)
      (hx (-> doc md/parse md/->hiccup))
      (hx [:pre.m-4 doc]))))

