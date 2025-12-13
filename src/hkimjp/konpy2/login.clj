(ns hkimjp.konpy2.login
  (:require
   [buddy.hashers :as hashers]
   [charred.api :as charred]
   [environ.core :refer [env]]
   ; [hato.client :as hc]
   [hiccup2.core :as h]
   [org.httpkit.client :as hk-client]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.konpy2.util :refer [user btn local-date now HH:mm]]
   [hkimjp.konpy2.response :refer [page]]))

(def l22 (or (env :auth) "https://l22.melt.kyutech.ac.jp"))

(defn login
  [request]
  (page
   [:div.mx-4
    [:div.font-bold.p-2 "LOGIN" (when (env :develop) " (DEVELOP)")]
    (when-let [flash (:flash request)]
      [:div {:class "text-red-500"} flash])
    [:div.p-1
     [:form {:method "post"}
      (h/raw (anti-forgery-field))
      [:input.border-1.px-1.rounded
       {:name "login" :placeholder "account" :autocomplete "username"}]
      [:span.mx-1 ""]
      [:input.border-1.px-1.rounded
       {:name         "password"
        :type         "password"
        :placeholder  "password"
        :autocomplete "current-password"}]
      [:button {:class btn} "LOGIN"]]]
    [:br]]))

(defn login!
  [{{:keys [login password]} :params}]
  (t/log! {:level :debug :id "login!" :msg (str login " " password)})
  (if (empty? (env :auth))
    (do
      (t/log! :info (str "no auth mode: " login))
      (-> (resp/redirect "/k/tasks")
          (assoc-in [:session :identity] login)))
    (try
      (let [pw (-> (hk-client/get (str l22 "/api/user/" login))
                   deref
                   :body
                   charred/read-json
                   (get "password"))]
        (if (hashers/check password pw)
          (do
            (t/log! :info (str "login success: " login))
            (c/lpush (format "kp2:login:%s" (local-date))
                     (str login " " (HH:mm (now)))) ;;
            (-> (resp/redirect "/k/tasks")
                (assoc-in [:session :identity] login)))
          (do
            (t/log! :info (str "login failed: " login))
            (-> (resp/redirect "/")
                (assoc :session {} :flash "login failed")))))
      (catch Exception e
        (t/log! :warn (.getMessage e))
        (-> (resp/redirect "/")
            (assoc :session {} :flash "enter login/password"))))))

(defn logout! [request]
  (t/log! {:level :info
           :id "logout!"
           :msg (user request)})
  (-> (resp/redirect "/")
      (assoc :session {})))
