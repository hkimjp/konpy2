(ns hkimjp.konpy2.restrictions
  (:require
   [hkimjp.carmine :as c]
   [hkimjp.konpy2.util :refer [local-time]]
   [hkimjp.konpy2.system :as sys]))

(defn- flash [user msg]
  (c/setex (format "kp2:%s:flash"  user) sys/kp2-flash msg))

; sys/kp2-flash
(defn- key- [what user]
  (format "kp2:%s:%s" what user))

(defn- key-comment [user]
  (key- "comment" user))

(defn- key-upload [user]
  (key- "upload" user))

(defn before-comment [user]
  (if-let [last-submission (c/get (key-comment user))]
    (do
      (flash user (format "しっかりコメント読んでからコメントする。%s" last-submission))
      false)
    true))

(defn after-comment [user]
  (let [key-interval (key-comment user)]
    (c/setex  key-interval sys/min-interval-comments (local-time))))

(defn before-upload [user]
  (if-let [last-submission (c/get (key-upload user))]
    (do
      (flash user (format "一題ずつ。自力で。%s" last-submission))
      false)
    true))

(defn after-upload [user]
  (let [key (key-upload user)]
    (c/setex key sys/min-interval-uploads (local-time))))

(comment
  (before-comment "h")
  (c/get "kp2:h:flash")
  (after-comment "h")
  (before-upload "h")
  (c/get "kp2:h:flash")
  (c/ttl "kp2:h:flash")
  (after-upload "h")

  :rcf)
