(ns hkimjp.konpy2.stats
  (:require
   [charred.api :as charred]
   [clojure.java.io :as io]
   [environ.core :refer [env]]
   [taoensso.telemere :as t]
   [hkimjp.datascript :as ds]))

(when-not (ds/conn?)
  (ds/start-or-restore {:url (env :datascript)}))

(def users
  (map :login (-> (io/resource "users.json")
                  (charred/read-json :key-fn keyword)
                  :users)))
(comment
  (.indexOf users "patinca-nu")
  :rcf)

(def answers-q
  '[:find [?e ...]
    :in $ ?author
    :where
    [?e :author ?author]
    [?e :answer/status "yes"]])

(defn answers-by [user]
  (-> (ds/qq answers-q user)
      count))

; (answers-by "pacinca-nu")

(defn- get-num [e]
  (let [answer (-> (ds/pl e)
                   :to
                   (ds/pl))]
    (if (zero? (:week answer))
      0
      (:num answer))))

(defn answers-with-weight [user]
  (-> (reduce +
              (for [e (ds/qq answers-q user)]
                (let [n (get-num e)]
                  (+ 1.0 (/ n 5)))))
      (* 100)
      int
      (/ 100.0)))

; (answers-with-weight "kiyodai")

(def comments-q
  '[:find [?e ...]
    :in $ ?author
    :where
    [?e :author ?author]
    [?e :comment/status "yes"]])

(defn comments-by [user]
  (-> (ds/qq comments-q user)
      count))

; (comments-by "hkimura")

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

(defn comments-with-weight [c cc]
  (if (zero? c)
    0.0
    (if (< 60 cc)
      (* c 1.2)
      (* 1.0 c))))

(comment
  (t/set-min-level! :info)

  (doseq [[score id] (-> (sort-by first
                                  (for [user users]
                                    [(comment-chars user) user]))
                         reverse)]
    (println (format "%8d %s" score id)))

  (doseq [user (sort (conj users "hkimura"))]
    (let
     [;;a (answers-by user)
      aw (answers-with-weight user)
      c (comments-by user)
      cc (comment-chars user)
      cw (comments-with-weight c cc)]
      (println  (format "%10s %5.1f %5.1f %5.1f" user aw cw (+ aw cw)))))

  :rcf)
