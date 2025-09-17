(ns user
  (:require
   [babashka.fs :as fs]
   [clojure.java.io :as io]
   [clj-reload.core :as reload]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.util :refer [now]]
   [hkimjp.konpy2.system :refer [start-system stop-system]]))

(t/set-min-level! :debug)

(start-system)

(defn restart-system
  []
  (stop-system)
  (start-system))

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

(defn problem! [w n txt]
  (ds/puts! [{:db/id -1
              :problem/status "yes"
              :week w
              :num n
              :problem txt
              :testcode ""
              :updated (now)}]))

(defn problems! [m txts]
  (doseq [[n txt] (map-indexed vector txts)]
    (problem! m n txt)))

(comment
  (problems!
   0
   ["タイピング練習を50回こなす。"
    "タイピング練習で最高点10点以上とる。"
    "Python で 1/1, 1/2, 1/3, 1/4, 1/5, 1/6, 1/7, 1/8, 1/9, 1/10をプリントしなさい。"
    "Python で apple, orange, banana, grape, melon, peach, pine を 一行に一つずつプリントしなさい。"
    "Python で |, /, -, \\, |, /, -, \\ をプリントしなさい。"
    "print( ) で九九の表をプリントしなさい。"])

  (c/ping)
  :rcf)

(comment
  (let [file (fs/create-temp-file {:suffix ".py"})]
    (spit (fs/file file) "print('use bb?\nhow is it?\n')")
    (println (slurp (fs/file file)))
    (fs/delete-if-exists (fs/file file))
    (fs/exists? (fs/file file)))
  :rcf)
