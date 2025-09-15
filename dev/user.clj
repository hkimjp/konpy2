(ns user
  (:require
   [clj-reload.core :as reload]
   [environ.core :refer [env]]
   [java-time.api :as jt]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.util :refer [today]]
   [hkimjp.konpy2.system :refer [start-system stop-system]]))

(t/set-min-level! :debug)

(defn restart-system
  []
  (stop-system)
  (start-system))

(start-system)
;; (restart-system)

(defn before-unload []
  (stop-system))

(defn after-reload []
  (start-system))

(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}
  :unload-hook 'before-unload
  :after-reload 'start-system})

;; (reload/reload)

(defn put [w n yn]
  (ds/puts! [{:db/id -1
              :problem/status yn
              :week w
              :num n
              :problem "python"
              :test "test code"
              :updated jt/local-date-time}]))

(comment
  (:user (ds/pl (parse-long "24")))

  (ds/qq '[:find ?e ?valid
           :where
           [?e :problem/valid ?valid]])
  (put 0 2 false)
  (def m (ds/pl 2))
  (keys m)

  (def data {:__anti-forgery-token "NWBjeO+Hg6pHpdtpdB8f6Uf1BSm3L46VUeZom/kAiGmP14hXRYTzAoBB1n3BytLqh5ytXXCcaUwj/pOP", "db/id" "2", "problem/valid" "true", :week "0", :num "1", :problem "10", :test "10", :gpt "10"})

  (:apple data)
  (data "apple")
  (-> data
      (dissoc :__anti-forgery-token "problem/valid"))
  :rcf)
