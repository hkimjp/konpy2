(ns hkimjp.konpy2.comments
  (:require
   [clojure.string :as str]
   [nextjournal.markdown :as md]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [hx redirect page]]
   [hkimjp.konpy2.restrictions :as r]
   [hkimjp.konpy2.util :refer [now user]]))

(defn comment!
  "send comments to `e`.
   returns clickable commenters list"
  [{{:keys [to author comment pid pt]} :params :as request}]
  (t/log! {:level :info
           :id    "comment!"
           :data  {:to to :author author :comment comment :pid pid :pt pt}})
  (let [author (user request)]
    (try
      (when-not (re-find #"\S" comment)
        (throw (Exception. "empty comment")))
      (when (< (count (str/split-lines comment)) 3)
        (throw (Exception. "need at least 3 lines")))
      (r/before-comment author)
      (ds/put! {:comment/status "yes"
                :author author
                :to (parse-long to)
                :comment comment
                :pt pt
                :updated (now)})
      (r/after-comment author)
      (redirect (str "/k/problem/" pid))
      (catch Exception ex
        (t/log! {:level :warn :data {:user author :ex (.getMessage ex)}})
        (page
         [:div.m-4
          [:div.text-2xl "Error"]
          [:p.text-red-600  (.getMessage ex)]
          [:p "your comment:"]
          [:pre.m-4 comment]])))))

(defn hx-comment [{{:keys [e]} :path-params :as request}]
  (t/log! {:level :info :id "hx-comment" :msg (user request)})
  (c/incr (r/key-comment-read (user request)))
  (hx [:div (-> (:comment (ds/pl (parse-long e)))
                md/parse
                md/->hiccup)]))
