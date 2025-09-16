(ns hkimjp.konpy2.comments
  (:require
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx redirect]]
   [hkimjp.konpy2.util :refer [now]]))

(def ^:private comments-to
  '[:find ?e ?author
    :in $ ?to
    :where
    [?e :comment/status "yes"]
    [?e :author ?author]
    [?e :to ?to]])

; (defn div-comments
;   "returns comments sent to `e`"
;   [e]
;   (t/log! {:level :info :id "comments-div" :data e})
;   [:div
;    (for [[e author] (ds/qq comments-to e)]
;      [:button.pr-4 author])])

; pid をもらってこないと戻るページがない
(defn post-comment
  "send comments to `e`.
   returns clickable commenter's list"
  [{{:keys [to author comment pid]} :params}]
  (t/log! {:level :info
           :id    "post-comment"
           :data  {:to to :author author :comment comment :pid pid}})
  (ds/put! {:comment/status "yes"
            :author author
            :to (parse-long to)
            :comment comment
            :updated (now)})
  (redirect (str "/k/problem/" pid)))

(defn show-comment [{{:keys [e]} :path-params}]
  (hx [:div (:comment  (ds/pl (parse-long e)))]))

; (show-comment {:path-params {:e "47"}})
