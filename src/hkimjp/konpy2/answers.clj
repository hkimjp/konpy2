(ns hkimjp.konpy2.answers
  (:require
   [environ.core :refer [env]]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.digest :refer [digest]]
   [hkimjp.konpy2.response :refer [page hx redirect]]
   [hkimjp.konpy2.restrictions :as r]
   [hkimjp.konpy2.util :refer [user now btn iso local-date]]
   [hkimjp.konpy2.validate :refer [validate]]))

(defn hx-answer [{{:keys [e p]} :path-params :as request}]
  (t/log! {:level :debug :id "hx-answer" :msg (str "e " e)})
  (let [author (user request)
        e (parse-long e)
        ans (ds/pl e)
        comments (ds/qq '[:find ?e ?author
                          :in $ ?to
                          :where
                          [?e :to ?to]
                          [?e :author ?author]
                          [?e :comment/status "yes"]]
                        e)]
    (hx [:div#answer.my-4
         [:div.flex.gap-4
          [:div {:class "w-1/2"}
           [:div [:span.font-bold "author: "]
            (if (or (= "chatgpt" (:author ans)) (= author (:author ans)))
              (:author ans)
              "******")]
           [:div [:span.font-bold "updated: "] (-> (:updated ans) str iso)]
           [:pre.border-1.p-2 (:answer ans)]]
          [:div.py-4 {:class "w-1/2"}
           [:div.py-4 [:span.font-bold "same: "] (:same ans)]
           [:div.py-2 [:span.font-bold "comments: "]
            (for [[eid author] (sort-by first comments)]
              [:button.pr-4.hover:underline
               {:hx-get (str "/k/comment/" eid)
                :hx-target "#comment"
                :hx-swap "innerHTML"}
               author])]
           [:div#comment.mx-4 "[comment]"]
           [:br]
           [:div.font-bold "your comment:"]
           (t/log! :debug (c/llen (format "kp2:%s:comments:%s" author (local-date))))
           (if (<= (parse-long (env :max-comments))
                   (c/llen (format "kp2:%s:comments:%s" author (local-date))))
             [:div.mx-4 (format "1日%sコメに達しました。" (env :max-comments))]
             [:form {:method "post" :action "/k/comment"}
              (h/raw (anti-forgery-field))
              [:input {:type "hidden" :name "to" :value e}]
              [:input {:type "hidden" :name "author" :value author}]
              [:input {:type "hidden" :name "pid" :value p}]
              [:textarea
               {:class "w-3/4 bg-lime-100 h-40 border-1 p-2"
                :name "comment"
                :placeholder "markdown OK"}]
              (for [pt ["A" "B" "C"]]
                [:button {:class btn :name "pt" :value pt} pt])])]]])))

(def ^:private same-answers
  '[:find ?author
    :in $ ?digest
    :where
    [?e :digest ?digest]
    [?e :author ?author]
    [?e :answer/status "yes"]])

(defn answer! [{{:keys [file e]} :params :as request}]
  (t/log! {:level :info :id "answer!"})
  (t/log! {:level :debug :data {:e e :file file}})
  (try
    (when (nil? file)
      (throw (Exception. "please select your python file.")))
    (let [author   (user request)
          answer   (slurp (:tempfile file))
          e        (parse-long e)
          entry    (ds/pl e)
          testcode (:testcode entry)
          doctest  (empty? (:doctest entry))
          _        (t/log! :debug (str "doctest " doctest))
          dgst     (digest answer)
          same     (->> (ds/qq same-answers dgst)
                        (map first)
                        (interpose " ")
                        (apply str))]
      (r/before-upload author)
      (validate author answer testcode doctest)
      (ds/put! {:answer/status "yes"
                :to      e
                :author  author
                :answer  answer
                :digest  dgst
                :same    same
                :updated (now)})
      (r/after-upload author)
      (redirect (str "/k/problem/" e)))
    (catch Exception e
      (t/log! {:level :warn :data {:exception (.getMessage e)}})
      (page
       [:div.m-4
        [:div.text-2xl "Error"]
        [:p.text-red-600 (h/raw (.getMessage e))]]))))

(def q
  '[:find ?answer
    :in $ ?author ?week ?num
    :where
    [?e :author ?author]
    [?e :to ?to]
    [?to :week ?week]
    [?to :num ?num]
    [?e :answer ?answer]
    [?e :answer/status "yes"]])

(defn download [{{:keys [author week num]} :path-params :as request}]
  (t/log! {:level :info :data (:path-params request)})
  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (str {:author author
               :week week
               :num num})})


