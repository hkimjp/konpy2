(ns hkimjp.konpy2.answers
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   ; [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.response :refer [page hx redirect]]
   [hkimjp.konpy2.restrictions :as r]
   [hkimjp.konpy2.util :refer [user now btn iso]]
   [hkimjp.konpy2.validate :refer [validate]]))

(def ^:private comments-to '[:find ?e ?author
                             :in $ ?to
                             :where
                             [?e :comment/status "yes"]
                             [?e :to ?to]
                             [?e :author ?author]])

(def ^:private gpt '[:find ?answer
                     :in $ ?to
                     :where
                     [?e :answer/status "yes"]
                     [?e :author "chatgpt"]
                     [?e :answer ?answer]
                     [?e :to ?to]])

(defn hx-answer [{{:keys [e p]} :path-params :as request}]
  (t/log! {:level :debug :id "hx-answer" :msg (str "e " e)})
  (let [author (user request)
        e (parse-long e)
        ans (ds/pl e)
        gpt-ans (-> (ds/qq gpt (parse-long p)) ffirst)
        comments (ds/qq comments-to e)]
    (hx [:div
         [:div.flex.gap-4
          [:div {:class "w-1/2"}
           [:div [:span.font-bold "author: "]
            (if (= author (:author ans)) author "******")]
           [:div [:span.font-bold "updated: "] (-> (:updated ans) str iso)]
           [:pre.border-1.p-2 (:answer ans)]
           [:div.font-bold "comments"]
           (for [[eid author] (sort-by first comments)]
             [:button.pr-4.hover:underline
              {:hx-get (str "/k/comment/" eid)
               :hx-target "#comment"
               :hx-swap "innerHTML"}
              author])]
          [:div {:class "w-1/2"}
           [:div [:span.font-bold "author: "] "chatgpt"]
           [:div [:span.font-bold "updated: "] "yyyy-mm-dd"]
           [:pre.border-1.p-2 gpt-ans]]]
         [:div#comment.mx-4 "[comment]"]
         [:div.font-bold "your comment"]
         [:form {:method "post" :action "/k/comment"}
          (h/raw (anti-forgery-field))
          [:input {:type "hidden" :name "to" :value e}]
          [:input {:type "hidden" :name "author" :value author}]
          [:input {:type "hidden" :name "pid" :value p}]
          [:textarea.border-1.p-2 {:class "w-2/3" :name "comment"}]
          (for [pt ["A" "B" "C"]]
            [:button {:class btn :name "pt" :value pt} pt])]])))

(defn answer! [{{:keys [file e]} :params :as request}]
  (t/log! {:level :info :id "answer!"})
  (t/log! {:level :debug :data {:e e :file file}})
  (let [author (user request)
        answer (slurp (:tempfile file))
        e (parse-long e)
        testcode (:testcode (ds/pl e))]
    (t/log! {:level :debug :data {:testcode testcode}})
    (try
      (r/before-upload author)
      (validate author answer testcode)
      (ds/put! {:answer/status "yes"
                :to      e
                :author  author
                :answer  answer
                :digest  0
                :updated (now)})
      (r/after-upload author)
      (redirect (str "/k/problem/" e))
      (catch Exception ex
        (t/log! {:level :warn :data {:exception (.getMessage ex)}})
        (page
         [:div
          [:div.text-2xl "Error"]
          [:p.text-red-600 (.getMessage ex)]])))))
