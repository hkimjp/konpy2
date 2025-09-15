(ns hkimjp.konpy2.comments
  (:require
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [now]]))

(defn comment
  "return comment eid=e"
  [{{:keys [e]} :path-params}]
  (let [e (parse-long e)
        comm (-> (ds/pl e)
                 :comment)]
    (t/log! {:level :info :id "comment" :data comm})
    (hx [:div comm])))

(comment {:path-params {:e "31"}})

; (defn comments-to
;   "returns comments sent to `e`"
;   [{{:keys [e]} :path-params}]
;   (t/log! {:level :info :id "get-comment" :data e})
;   (hx [:div "user1 user2 user3"]))

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
