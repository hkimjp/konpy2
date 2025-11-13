(ns hkimjp.konpy2.answers
  (:require
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

(def comments-q '[:find ?e ?author
                  :in $ ?to
                  :where
                  [?e :to ?to]
                  [?e :author ?author]
                  [?e :comment/status "yes"]])

(defn hx-answer [{{:keys [e p]} :path-params :as request}]
  (t/log! {:level :debug :id "hx-answer" :data {:e e :p p}})
  (let [user (user request)
        e (parse-long e)
        comments (ds/qq comments-q e)
        ans (ds/pl e)
        author (:author ans)
        p (parse-long p)
        ; {:keys [week num]} (ds/pl '[:week :num] p)
        ]
    (hx
     [:div#answer.my-4.flex.gap-4
      ; left half, answer
      [:div {:class "w-1/2"}
       [:div [:span.font-bold "author: "]
        (if (or (= "chatgpt" author) (= user author))
          author
          "******")]
       [:div [:span.font-bold "same: "] (:same ans)]
       [:div [:span.font-bold "updated: "] (-> (:updated ans) str iso)]
       [:pre.border-1.p-2 {:white-space "pre-wrap"} (:answer ans)]
       [:div [:a {:class btn
                  ; :href (format "/download/%s/%d/%d" author week num)
                  :href (format "/dl/%d" e)
                  :hx-boost "false"}
              "download"]]]
      ; right half, comments
      [:div {:class "w-1/2 white"}
       [:div.box-content.size-16 ""]
       [:div.py-2 [:span.font-bold "comments: "]
        (for [[eid author] (sort-by first comments)]
          [:button.pr-4.hover:underline
           {:hx-get (str "/k/comment/" eid)
            :hx-target "#comment"
            :hx-swap "innerHTML"}
           author])
        [:div#comment.mx-4 "[comment]"]]
       [:div
        [:div.font-bold "your comment:"]
        (if (<= r/max-comments (c/llen (format "kp2:%s:comments:%s" author (local-date))))
          [:div.mx-4 (format "1日%sコメに達しました。" r/max-comments)]
          [:div
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
              [:button {:class btn :name "pt" :value pt} pt])]])]]])))

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

(def ^:private author-week-num-q
  '[:find [?author ?week ?num]
    :in $ ?e
    :where
    [?e :to ?p]
    [?e :author ?author]
    [?p :week ?week]
    [?p :num ?num]])

(defn dl [{{:keys [eid]} :path-params}]
  (let [eid (parse-long eid)
        [author week num] (ds/qq author-week-num-q eid)
        filename (format "%s-%d-%d.py" author week num)]
    (t/log! {:level :info :id "dl" :date {:eid eid}})
    {:status 200
     :headers {"Content-Disposition"
               (format "attachment; filename=\"%s\"" filename)}
     :body  (:answer (ds/pl eid))}))

(comment
  (ds/pl 2210)
  (ds/qq week-num-q 2210)
  (let [[week num] (ds/qq week-num-q 2210)]
    [week num])
  (ds/qq week-num-q 2210)
  (ds/qq '[:find ?e
           :where
           [?e :problem/status "yes"]])
  (dl {:path-params {:eid "2210"}})
  :rcf)

(def download-q
  '[:find [?answer]
    :in $ ?author ?week ?num
    :where
    [?e :author ?author]
    [?e :to ?to]
    [?to :week ?week]
    [?to :num ?num]
    [?e :answer ?answer]
    [?e :answer/status "yes"]])

(defn download [{{:keys [author week num]} :path-params}]
  (t/log! {:level :info :data (:author author :week week :num num)})
  (let [week (parse-long week)
        num (parse-long num)
        [answer] (ds/qq download-q author week num)
        filename (format "%s_%d_%d.py" author week num)]
    {:status 200
     :headers {"Content-Disposition"
               (format "attachment; filename=\"%s\"" filename)}
     :body answer}))
