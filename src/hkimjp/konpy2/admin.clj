(ns hkimjp.konpy2.admin
  (:require
   [clojure.string :as str]
   [environ.core :refer [env]]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.login :refer [l22]]
   [hkimjp.konpy2.response :refer [page redirect hx]]
   [hkimjp.konpy2.restrictions :as r]
   [hkimjp.konpy2.util :refer [btn user now abbrev]]
   [hkimjp.konpy2.validate :as v]))

(defn- section [title]
  [:div.font-bold title])

(defn- input-box [label val]
  [:input.text-center.size-6.outline {:name label :value val}])

(defn- problem-form
  [{:keys [db/id problem/status week num problem testcode doctest updated] :as params}]
  (t/log! {:level :info :id "problem-form"})
  (t/log! {:level :debug :data params})
  (t/log! {:level :debug :data {:id id :status status}})
  [:div.m-4
   [:div.text-2xl.font-bold "Problem"]
   [:form.mx-4 {:method "post"}
    (h/raw (anti-forgery-field))
    [:input {:type "hidden" :name "id" :value id}]
    (section "problem/status")
    [:input (merge {:type "radio" :name "status" :value "yes"}
                   (when (= status "yes") {:checked "checked"})) "yes "]
    [:input (merge {:type "radio" :name "status" :value "no"}
                   (when (= status "no")  {:checked "checked"})) "no "]
    (section "week-num")
    [:div (input-box "week" week) " - " (input-box "num" num)]
    (section  "problem")
    [:textarea.w-full.h-20.p-2.border-1 {:name "problem"} problem]
    (section "skip-doctest")
    (input-box "doctest" doctest) [:span.mx-4 "leave brank if want doctest)"]
    (section  "testcode")
    [:textarea.w-full.h-40.p-2.border-1 {:name "testcode"} testcode]
    (section  "updated")
    [:div updated]
    [:br]
    [:div [:button {:class btn} "upsert"]]
    [:br]]])

(defn upsert! [{params :params}]
  (let [{:keys [id status week num problem doctest testcode]} params
        id (if (= -1 id) -1 (parse-long id))
        data {:db/id id
              :problem/status status
              :week (parse-long week)
              :num  (parse-long num)
              :problem problem
              :doctest doctest
              :testcode testcode
              :updated (now)}]
    (t/log! {:level :debug :data data})
    (try
      (ds/put! data)
      (redirect "/admin/problems")
      (catch Exception e
        (t/log! {:level :error :msg e})))))

(defn- first-n [s n]
  (-> (str/split-lines s)
      first
      (abbrev n)))

(defn- problems-section []
  (let [get-problems
        '[:find ?e ?week ?num ?problem ?testcode ?updated
          :keys e  week  num  problem  testcode  updated
          :where
          [?e :week ?week]
          [?e :num ?num]
          [?e :problem/status "yes"]
          [?e :problem ?problem]
          [?e :testcode ?testcode]
          [?e :updated ?updated]]]
    [:div
     [:div.text-2xl.font-bold "Problems"]
     [:div.m-4
      [:div [:a {:class btn :href "/admin/new"} "new"]]
      [:div
       (for [p (->> (ds/qq get-problems)
                    (sort-by (juxt (fn [x] (* -1 (:week x))) :num)))]
         [:div
          [:a.hover:underline {:href (str "/admin/update/" (:e p))}
           [:div.flex.gap-4
            [:div (:week p) "-" (:num p)]
            #_[:div (:status p)]
            [:div {:class "w-2/3"} (-> (:problem p)  (first-n 40))]
            [:div {:class "w-1/4"} (-> (:testcode p) (first-n 20))]]]])]]]))

(defn- env-vars-section []
  [:div
   [:div.text-2xl.font-bold "Env Vars"]
   [:div.m-4
    [:p "develop: " (env :develop)]
    [:p "start-day:" (env :start-day)]
    [:p "auth: " l22]
    [:p "port: " (env :port)]
    [:p "admin: " (env :admin)]
    [:p "datascript: " (env :datascript)]
    [:p "redis: " (env :redis)]
    [:p "max-comments: " r/max-comments]
    [:p "max-uploads: "  r/max-uploads]
    [:p "min-interval-comments: " r/min-interval-comments]
    [:p "min-interval-uploads: " r/min-interval-uploads]
    [:p "must-read-before-upload: " r/must-read-before-upload]
    [:p "must-write-berfore-upload: " r/must-write-before-upload]]])

(defn- redis-vars-section
  "also includes Paths section"
  [user]
  [:div
   ; Redis
   [:div.text-2xl.font-bold "Redis Vars"]
   [:div.m-4
    (for [key ((juxt r/key-comment-read r/key-comment-write) user)]
      [:div.flex.gap-4 [:div key] [:div (c/get key)]])
    (for [key ((juxt r/key-comment r/key-upload) user)]
      [:div.flex.gap-4 [:div key] [:div (c/ttl key)]])
    (for [key ((juxt r/key-comments r/key-uploads) user)]
      [:div.flex.gap-4
       [:div key]
       [:div (if-let [uploads (c/lrange key)]
               (str uploads)
               "NIL")]])]
   ; Paths
   [:div.text-2xl.font-bold "Paths"]
   [:div.m-4
    [:div.flex.gap-4
     [:div "python"] [:div v/python-path]]
    [:div.flex.gap-4
     [:div "pytest"] [:div v/pytest-path]]
    [:div.flex.gap-4
     [:div "ruff"] [:div v/ruff-path]]]])

(defn- delete-section []
  [:div
   [:div.text-2xl.font-bold "Find eid"]
   [:div.m-4
    [:form
     (h/raw (anti-forgery-field))
     [:input.border-1 {:name "author" :placeholder "author"}]
     [:input.border-1 {:name "week" :placeholder "week"}]
     [:input.border-1 {:name "num" :placeholder "num"}]
     [:buttn
      {:class btn
       :hx-post "/admin/eid"
       :hx-target "#eid"
       :hx-swap "innerHTML"}
      "find"]]
    [:div#eid "eid"]]
   [:div.text-2xl.font-bold "Delete"]
   [:form.m-4
    (h/raw (anti-forgery-field))
    [:input.border-1 {:name "eid" :placeholder "eid"}]
    [:button {:class btn
              :hx-post "/admin/delete"
              :hx-swap "none"}
     "delete"]]])

(defn admin [request]
  (t/log! {:level :info :id "problems" :msg (user request)})
  (page
   [:div.m-4
    (problems-section)
    [:br]
    (delete-section)
    [:div.flex
     (env-vars-section)
     (redis-vars-section (user request))]]))

(defn new [request]
  (t/log! {:lelvel :info :id "new" :msg (user request)})
  (page
   (problem-form {:db/id -1
                  :problem/status "yes"
                  :week ""
                  :num ""
                  :problem ""
                  :testcode ""})))

(defn edit [{{:keys [e]} :path-params}]
  (t/log! {:level :info :id "edit" :data {:e e}})
  (page
   (problem-form (ds/pl (parse-long e)))))

(def ^:private eid-q
  '[:find [?a ...]
    :in $ ?author ?week ?num
    :where
    [?a :author ?author]
    [?a :answer/status "yes"]
    [?a :to ?e]
    [?e :week ?week]
    [?e :num ?num]])

(defn eid [{{:keys [author week num]} :params :as request}]
  (t/log! {:level :info :id "eid"
           :data (dissoc (:params request) :__anti-forgery-token)})
  (let [ret (ds/qq eid-q author (parse-long week) (parse-long num))]
    (t/log! {:level :debug :msg ret})
    (hx [:div (str "found: " ret)])))

(defn delete! [{{:keys [eid]} :params}]
  (t/log! {:level :info :id "delete" :msg (str "eid: " eid)})
  (ds/put! {:db/id (parse-long eid) :answer/status "delete"})
  (hx [:div (str "delete " eid)]))
