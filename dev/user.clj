(ns user
  (:require
   [clj-reload.core :as reload]
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [java-time.api :as jt]
   [taoensso.telemere :as tel]
   [hkimjp.datascript :as ds]
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

; --------------------------------
; midterm daily points aggregation

(comment
  (tel/set-min-level! :info)

  (def record (for [user (-> (slurp (io/resource "users.txt"))
                             edn/read-string)]
                [user (count (q/answers user)) (count (q/sent user))]))

  (doseq [u (-> (sort-by (fn [[_ ans com]] (+ ans com)) record)
                reverse)]
    (println u))
  :rcf)

; ------------------------------
; after midterm exam, who's doing konpy?

(tel/set-min-level! :info)

(jt/before? (jt/local-date-time 2025 12 2) (jt/local-date-time))
; => true

(def q
  '[:find (count ?e)
    :in $ ?midterm ?author
    :where
    [?e :author ?author]
    [?e :answer/status "yes"]
    [?e :updated ?updated]
    [(jt/before? ?midterm ?updated)]])

(defn since-mx [user]
  (if-let [ret (-> (ds/qq q (jt/local-date-time 2025 12 2) user) first)]
    (first ret)
    0))

; (since-mx "waku1waku2")
; => 17

(def users (->> (slurp (io/resource "midterm-ex.txt"))
                str/split-lines
                (mapv #(str/split % #"\s"))
                (map first)))

(doseq [u users]
  (println (format "%2d %s" (since-mx u) u)))
