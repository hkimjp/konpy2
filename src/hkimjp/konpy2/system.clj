(ns hkimjp.konpy2.system
  (:require
   [environ.core :refer [env]]
   [ring.adapter.jetty :as jetty]
   [taoensso.telemere :as t]
   [hkimjp.konpy2.routes :as routes]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]))

(defonce server (atom nil))

;; period restriction in second.
;; 86400 = (* 24 60 60)
(def min-interval-answers  (-> (or (env :min-interval-answer) "60") parse-long))
(def min-interval-comments (-> (or (env :min-inverval-comments) "60") parse-long))
(def min-interval-uploads  (-> (or (env :min-inverval-uploads) "60") parse-long))
(def max-comments (-> (or (env :max-comments) "86400") parse-long))
(def max-uploads  (-> (or (env :max-uploads) "86400") parse-long))
(def kp2-flash (-> (or (env :flash) "3") parse-long))

(defn start-jetty
  []
  (let [port (parse-long (or (env :port) "3000"))
        handler (if (some? (env :develop))
                  #'routes/root-handler
                  routes/root-handler)]
    (reset! server (jetty/run-jetty handler {:port port :join? false}))
    (t/log! :info (str "server started at port " port))))

(defn stop-server []
  (when @server
    (.stop @server)
    (t/log! :info "server stopped.")))

(defn start-system []
  (t/log! {:level :info
           :id "start-system"
           :msg (env :develop)
           :data {:redis (env :redis)
                  :datascript (env :datascript)}})
  (try
    (c/redis-server (env :redis))
    (ds/start-or-restore {:url (env :datascript)})
    (start-jetty)
    (catch Exception e
      (t/log! :fatal (.getMessage e))
      (System/exit 0))))

(defn stop-system []
  (stop-server)
  (ds/stop))

