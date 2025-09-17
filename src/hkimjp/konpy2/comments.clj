(ns hkimjp.konpy2.comments
  (:require
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [hx redirect]]
   [hkimjp.konpy2.util :refer [now]]))

(defn comment!
  "send comments to `e`.
   returns clickable commenters list"
  [{{:keys [to author comment pid]} :params}]
  (t/log! {:level :info
           :id    "comment!"
           :data  {:to to :author author :comment comment :pid pid}})
  (ds/put! {:comment/status "yes"
            :author author
            :to (parse-long to)
            :comment comment
            :updated (now)})
  (redirect (str "/k/problem/" pid)))

(defn hx-comment [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "hx-comment"})
  (hx [:div (:comment  (ds/pl (parse-long e)))]))

; (hx-comment {:path-params {:e "47"}})
