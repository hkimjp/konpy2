(ns user
  (:require
   [clj-reload.core :as reload]
   [taoensso.telemere :as tel]
   [hkimjp.konpy2.system :as sys :refer [restart-system]]))

(tel/set-min-level! :debug)

(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}
  :unload-hook 'before-unload
  :after-reload 'start-system})

(defn before-unload []
  (sys/stop-system))

(defn after-reload []
  (sys/start-system))

;; (reload/reload)

; (sys/stop-system)
(sys/start-system)

