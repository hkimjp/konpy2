(ns hkimjp.konpy2.comments
  (:require
   [taoensso.telemere :as t]
   [hkimjp.konpy2.response :refer [page hx]]))

(defn comments
  "returns comments sent to `e`"
  [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "get-comment" :data e})
  (hx [:div "user1 user2 user3"]))

(defn post-comment
  "403 forbidden. why?
   send comments to `e`.
   returns clickable commenter's list"
  [{params :params}]
  (t/log! {:level :info :id "post-comment" :data params})
  (page [:div "under construction"]))

