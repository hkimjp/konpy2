(ns hkimjp.konpy2.middleware
  (:require
   [environ.core :refer [env]]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]
   [hkimjp.konpy2.util :refer [user]]))

(defn wrap-users
  [handler]
  (fn [request]
    (let [user (user request)]
      (t/log! :debug (str "wrap-users " user))
      (if (some? user)
        (do
          (t/log! :debug "found")
          (handler request))
        (do
          (t/log! :debug "not found")
          (-> (resp/redirect "/login")
              (assoc :session {} :flash "need login")))))))

(defn wrap-admin [handler]
  (fn [request]
    (let [user (user request)]
      (t/log! :debug (str "wrap-admin " user))
      (if (= (env :admin) user)
        (handler request)
        (-> (resp/redirect "/login")
            (assoc :session {} :flash "admin only"))))))
