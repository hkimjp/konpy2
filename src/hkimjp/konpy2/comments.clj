(ns hkimjp.konpy2.comments
  (:require
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [now]]))

(defn comments
  "returns comments sent to `e`"
  [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "get-comment" :data e})
  (hx [:div "user1 user2 user3"]))

(defn post-comment
  "send comments to `e`.
   returns clickable commenter's list"
  [{params :params}]
  (t/log! {:level :info :id "post-comment" :data params})
  (try
    (ds/put! {:comment/status "yes"
              :author (:author params)
              :to (parse-long (:e params))
              :comment (:comment params)
              :updated (now)})
    (page [:div "under construction"])
    (catch Exception e
      (t/log! :error e))))

(comment
  (ds/qq '[:find ?e
           :where
           [?e :comments/status "yes"]])
  (ds/pl 26)
  (ds/pl 25)
  (ds/pl 27)
  :rcf)
