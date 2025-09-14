(ns hkimjp.konpy2.answers
  (:require
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]))

(defn get-answers [{{:keys [e]} :path-parms}]
  (t/log! {:level :info :id get-answers :data e})
  (hx [:div "get-answers"]))

(defn post-answer [{{:keys [file e]} :params :as request}]
  (t/log! {:level :info :id "post-answer"})
  (t/log! {:level :debug :data {:e e :file file}})
  (try

    (throw (Exception. "under construction"))
    (catch Exception e
      (t/log! {:level :error :data file})
      (page
       [:div
        [:div.text-2xl.text-red-600 "Error"]
        [:p (.getMessage e)]]))))

