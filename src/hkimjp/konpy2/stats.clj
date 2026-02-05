(ns hkimjp.konpy2.stats
  (:require
   [charred.api :as charred]
   [clojure.java.io :as io]
   [environ.core :refer [env]]
   #_[taoensso.telemere :as t]
   [hkimjp.datascript :as ds]))

(def users
  (->> (charred/read-json (io/resource "users.json") :key-fn keyword)
       :users
       (map :login)))

(ds/start-or-restore {:url (env :datascript)})

(def answers-q
  '[:find [?e ...]
    :in $ ?author
    :where
    [?e :author ?author]
    [?e :answer/status "yes"]
    #_[?e :to ?to]])

(def comments-q
  '[:find [?e ...]
    :in $ ?author
    :where
    [?e :author ?author]
    [?e :comment/status "yes"]
    #_[?e :to ?to]])

(def comment-chars-q
  '[:find [?comment ...]
    :in $ ?author
    :where
    [?e :author ?author]
    [?e :comment/status "yes"]
    [?e :comment ?comment]])

(defn comment-chars [user]
  (->> (ds/qq comment-chars-q user)
       (map count)
       (reduce +)))

; (comment-chars "hkimura")
; => 88975

(doseq [[score id] (-> (sort-by first
                                (for [user users]
                                  [(comment-chars user) user]))
                       reverse)]
  (println (format "%8d %s" score id)))
