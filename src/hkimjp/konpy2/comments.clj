(ns hkimjp.konpy2.comments
  (:require
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [hx redirect error-page]]
   [hkimjp.konpy2.util :refer [now user]]
   [hkimjp.konpy2.restrictions :as r]))

(defn comment!
  "send comments to `e`.
   returns clickable commenters list"
  [{{:keys [to author comment pid]} :params :as request}]
  (t/log! {:level :info
           :id    "comment!"
           :data  {:to to :author author :comment comment :pid pid}})
  (if (r/before-comment (user request))
    (do
      (ds/put! {:comment/status "yes"
                :author author
                :to (parse-long to)
                :comment comment
                :updated (now)})
      (r/after-comment (user request))
      (redirect (str "/k/problem/" pid)))
    (error-page (user request) "きちんと回答、コメント読んでコメントしないと。")))

(defn hx-comment [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "hx-comment"})
  (hx [:div (:comment (ds/pl (parse-long e)))]))

; (hx-comment {:path-params {:e "47"}})
