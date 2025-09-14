(ns hkimjp.konpy2.answers
  (:require
   [taoensso.telemere :as t]
   [hkimjp.konpy2.response :refer [hx]]))

(defn get-answers [{{:keys [e]} :path-parms}]
  (t/log! {:level :info :id get-answers :data e})
  (hx [:div "get-answers"]))

(defn post-answer [{params :params :as request}]
  (t/log! {:level :info :id "post-answer" :data params})
  (hx [:dic "post-answer"]))

