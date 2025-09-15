(ns hkimjp.konpy2.comments
  (:require
   [ring.util.response :as resp]
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

; (ds/qq comments-to 26)
; (ds/pl 28)

; (defn comments [{params :path-params}])

(defn div-comments
  "returns comments sent to `e`"
  [e]
  (t/log! {:level :info :id "comments-div" :data e})
  [:div
   (for [[e author] (ds/qq comments-to e)]
     [:button.pr-4 author])])

; (div-comments 26)

; pid をもらってこないと戻るページがない
(defn post-comment
  "send comments to `e`.
   returns clickable commenter's list"
  [{{:keys [to author comment]} :params}]
  (t/log! {:level :info
           :id    "post-comment"
           :data  {:to to :author author :comment comment}})
  (ds/put! {:comment/status "yes"
            :author author
            :to (parse-long to)
            :comment comment
            :updated (now)})
  (redirect "/k/problem/1"))

