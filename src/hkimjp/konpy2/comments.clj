(ns hkimjp.konpy2.comments
  (:require
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [hx redirect page]]
   [hkimjp.konpy2.util :refer [now user]]
   [hkimjp.konpy2.restrictions :as r]))

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
         [:div
          [:div.text-2xl "Error"]
          [:p.text-red-600  (.getMessage ex)]])))))

(defn hx-comment [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "hx-comment"})
  (hx [:div (:comment (ds/pl (parse-long e)))]))

