(ns hkimjp.konpy2.system
  (:require
   [environ.core :refer [env]]
   [org.httpkit.server :as hk]
   [taoensso.telemere :as t]
   [hkimjp.konpy2.routes :as routes]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]))

(defonce server (atom nil))

(defn start-server []
  (let [port (parse-long (or (env :port) "3000"))
        handler (if (some? (env :develop))
                  #'routes/root-handler
                  routes/root-handler)]
    (reset! server (hk/run-server handler {:port port :join? false}))
    (t/log! :info (str "server started at port " port))))

(defn stop-server []
  (when @server
    (@server)
    (reset! server nil)
    (t/log! :info "server stopped.")))

(defn start-system []
  (t/log! {:level :info
           :id "start-system"
           :msg (env :develop)
           :data {:redis (env :redis)
                  :datascript (env :datascript)}})
  (try
    (c/create-conn (env :redis))
    (ds/start-or-restore {:url (env :datascript)})
    (start-server)
    (catch Exception e
      (t/log! :fatal (.getMessage e))
      (System/exit 0))))

(defn stop-system []
  (stop-server)
  (ds/stop))

(defn restart-system []
  (stop-system)
  (start-system))
