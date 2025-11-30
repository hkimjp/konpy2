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

; ----------------

(ds/conn?)

(ds/pull @ds/conn ['*] 100)

; why this can't?
(ds/q @ds/conn '[:find ?e
                 :where
                 [?e]])

(ds/qq '[:find (count ?e)
         :where [?e]])

(def problems (ds/qq '[:find ?week ?num ?problem
                       :keys week  num  problem
                       :where
                       [?e :week ?week]
                       [?e :num ?num]
                       [?e :problem ?problem]]))

(sort-by :week problems)

(sort-by (fn [x] (:week x)) problems)

(spit (io/file "konpy.txt") (sort-by (fn [x] [(:week x) (:num x)]) problems))
