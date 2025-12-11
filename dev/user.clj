(ns user
  (:require
   [clj-reload.core :as reload]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [taoensso.telemere :as tel]
   [hkimjp.konpy2.queries :as q]
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

;; CHECK:(reload/reload) does not invoke :unload-hook nor :after-reload
;; (reload/reload)

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

; -------------------------------
; midter daily points aggregation

(tel/set-min-level! :info)

(def record (for [user (-> (slurp (io/resource "users.txt"))
                           edn/read-string)]
              [user (count (q/answers user)) (count (q/sent user))]))

(doseq [u (-> (sort-by (fn [[_ ans com]] (+ ans com)) record)
              reverse)]
  (println u))

