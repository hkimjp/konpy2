(ns hkimjp.konpy2.answers
  (:require
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.digest :refer [digest]]
   [hkimjp.konpy2.response :refer [page hx redirect]]
   [hkimjp.konpy2.restrictions :as r]
   [hkimjp.konpy2.util :refer [user now btn iso]]
   [hkimjp.konpy2.validate :refer [validate]]))

(defn hx-answer [{{:keys [e p]} :path-params :as request}]
  (t/log! {:level :debug :id "hx-answer" :msg (str "e " e)})
  (let [author (user request)
        e (parse-long e)
        ans (ds/pl e)
        gpt-ans (-> (ds/qq '[:find ?answer
                             :in $ ?to
                             :where
                             [?e :answer/status "yes"]
                             [?e :author "chatgpt"]
                             [?e :answer ?answer]
                             [?e :to ?to]] (parse-long p)) ffirst)
        comments (ds/qq '[:find ?e ?author
                          :in $ ?to
                          :where
                          [?e :comment/status "yes"]
                          [?e :to ?to]
                          [?e :author ?author]]
                        e)]
    (hx [:div
         [:div.flex.gap-4
          [:div {:class "w-1/2"}
           [:div [:span.font-bold "author: "]
            (if (= author (:author ans)) author "******")]
           [:div [:span.font-bold "updated: "] (-> (:updated ans) str iso)]
           [:pre.border-1.p-2 (:answer ans)]
           [:div [:span.font-bold "same: "] (:same ans)]
           [:div [:span.font-bold "comments: "]
            (for [[eid author] (sort-by first comments)]
              [:button.pr-4.hover:underline
               {:hx-get (str "/k/comment/" eid)
                :hx-target "#comment"
                :hx-swap "innerHTML"}
               author])]
           [:div#comment.mx-4 "[comment]"]]
          [:div {:class "w-1/2"}
           [:div [:span.font-bold "author: "] "chatgpt"]
           [:div [:span.font-bold "updated: "] ""]
           [:pre.border-1.p-2 gpt-ans]
           [:br]
           [:div.font-bold "your comment"]
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
              [:button {:class btn :name "pt" :value pt} pt])]]]])))

(def ^:private same-answers
  '[:find ?author
    :in $ ?digest
    :where
    [?e :answer/status "yes"]
    [?e :digest ?digest]
    [?e :author ?author]])

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
