(ns hkimjp.konpy2.answers
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx]]
   [hkimjp.konpy2.util :refer [user now btn]]))

(defn show-answer [{{:keys [e]} :path-params :as request}]
  (t/log! {:level :info :id "show-answer" :data e})
  (let [ans (ds/pl (parse-long e))]
    (hx [:div
         [:div [:span.font-bold "author: "] (:author ans)]
         [:div [:span.font-bold "updated: "] (:updated ans)]
         [:pre.border-1.p-2 (:answer ans)]
         [:div.font-bold "comments"]
         [:div#comments "[comments]"]
         [:div.font-bold "your comment"]
         [:form {:method "post" :action "/k/comment"}
          (h/raw (anti-forgery-field))
          [:input {:type "hidden" :name "e" :value e}]
          [:input {:type "hidden" :name "author" :value (user request)}]
          [:textarea.border-1.p-2 {:name "comment"}]
          [:button {:class btn} "send"]]])))

(defn post-answer [{{:keys [file e]} :params :as request}]
  (t/log! {:level :info :id "post-answer"})
  (t/log! {:level :debug :data {:e e :file file}})
  (try
    (ds/put! {:answer/status "yes"
              :to      (parse-long e)
              :author  (user request)
              :answer  (slurp (:tempfile file))
              :digest  0
              :updated (now)})
    (resp/redirect (str "/k/problem/" e))
    (catch Exception ex
      (t/log! {:level :error :data file})
      (page
       [:div
        [:div.text-2xl.text-red-600 "Error"]
        [:p (.getMessage ex)]]))))
