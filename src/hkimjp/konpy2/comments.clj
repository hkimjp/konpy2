(ns hkimjp.konpy2.comments
  (:require
   [ring.util.response :as resp]
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

(def ^:private problem-eid '[:find ?pid
                             :in $ ?cid
                             :where
                             [?cid :to ?pid]])

;; (-> (ds/qq problem-eid 23) first)

(defn post-comment
  "send comments to `e`.
   returns clickable commenter's list"
  [{{:keys [author e comment]} :params}]
  (t/log! {:level :info :id "post-comment" :data (parse-long e)})
  (let [e (parse-long e)]
    (try
      (ds/put! {:comment/status "yes"
                :author author
                :to e
                :comment comment
                :updated (now)})
      (resp/redirect (str "/k/problem/" (-> (ds/qq problem-eid e) ffirst)))
      (catch Exception ex
        (t/log! :error ex)))))
