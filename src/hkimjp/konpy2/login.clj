(ns hkimjp.konpy2.login
  (:require
   [buddy.hashers :as hashers]
   [environ.core :refer [env]]
   [hato.client :as hc]
   [hiccup2.core :as h]
   [ring.util.anti-forgery :refer [anti-forgery-field]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.konpy2.util :refer [user]]
   [hkimjp.konpy2.response :refer [page]]))

(def l22 (or (env :auth) "https://l22.melt.kyutech.ac.jp"))

(defn login
  [request]
  (page
   [:div.mx-4
    [:div.font-bold.p-2 "LOGIN"
     (when (env :develop) " (DEVELOP)")]
    (when-let [flash (:flash request)]
      [:div {:class "text-red-500"} flash])
    [:div.p-1
     [:form {:method "post"}
      (h/raw (anti-forgery-field))
      [:input.border-1.border-solid.px-1 {:name "login" :placeholder "account" :autocomplete "username"}]
      [:span.mx-1 ""]
      [:input.border-1.border-solid.px-1 {:name "password" :type "password" :placeholder "password" :autocomplete "current-password"}]
      [:button.mx-1.px-1.text-white.bg-sky-500.hover:bg-sky-700.active:bg-red-500.rounded-xl "LOGIN"]]]
    [:br]]))

(defn login!
  [{{:keys [login password]} :params}]
  ;; always login success when (env :auth) is empty
  (if (empty? (env :auth))
    (do
      (t/log! :info (str "no auth mode: " login))
      (-> (resp/redirect "/tasks")
          (assoc-in [:session :identity] login)))
    (try
      (let [resp (hc/get (str l22 "/api/user/" login) {:timeout 3000 :as :json})]
        (if (and (some? resp)
                 (hashers/check password (get-in resp [:body :password])))
          (do
            (t/log! :info (str "login success: " login))
            (-> (resp/redirect "/tasks")
                (assoc-in [:session :identity] login)))
          (do
            (t/log! :info (str "login failed: " login))
            (-> (resp/redirect "/login")
                (assoc :session {} :flash "login failed")))))
      ;; happens?
      (catch Exception e
        (t/log! :warn (.getMessage e))
        (-> (resp/redirect "/login")
            (assoc :session {} :flash "enter login/password"))))))

(defn logout!
  [request]
  (t/log! :info (str "logout! " (user request)))
  (-> (resp/redirect "/login")
      (assoc :session {})))
