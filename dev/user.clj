(ns user
  (:require
   [clj-reload.core :as reload]
   [taoensso.telemere :as tel]
   [hkimjp.konpy2.system :refer [start-system stop-system restart-system]]))

(tel/set-min-level! :debug)
; (stop-system)
(restart-system)

;--- clj-reload ---

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


