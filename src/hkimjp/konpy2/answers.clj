(ns hkimjp.konpy2.answers
  (:require
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [user now]]))

; (defn show-answer [{{:keys [e]} :path-parms}]
;   (t/log! {:level :info :id "answer" :data e})
;   (hx [:div "answer todays"]))

; (defn post-answer [{{:keys [file e]} :params :as request}]
;   (t/log! {:level :info :id "post-answer"})
;   (t/log! {:level :debug :data {:e e :file file}})
;   (try
;     (ds/put! {:answer/valid true
;               :to (parse-long e)
;               :user (user request)
;               :answer (slurp (:tempfile file))
;               :digest 0
;               :updated (now)})
;     (resp/redirect (str "/k/problem/" e))
;     (catch Exception e
;       (t/log! {:level :error :data file})
;       (page
;        [:div
;         [:div.text-2xl.text-red-600 "Error"]
;         [:p (.getMessage e)]]))))

