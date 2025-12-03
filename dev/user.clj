(ns user
  (:require
   [clj-reload.core :as reload]
   [clojure.java.io :as io]
   [taoensso.telemere :as tel]
   [hkimjp.datascript :as ds]
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

;; (reload/reload) does not invoke :unload-hook nor :after-reload
(defn reload []
  (stop-system)
  (reload/reload)
  (start-system))

; (reload)

; ----------------

; CHECK: whit this can't?
#_(ds/q @ds/conn '[:find ?e
                   :where
                   [?e]])

