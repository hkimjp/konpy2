(ns hkimjp.konpy2.restrictions
  (:require
   [environ.core :refer [env]]
   [hkimjp.carmine :as c]
   [hkimjp.konpy2.util :refer [local-time]]
   [hkimjp.konpy2.response :refer [page]]
   ;;[hkimjp.konpy2.system :as sys]
   ))

;; period restriction in second.
;; 86400 = (* 24 60 60)
; (def min-interval-answers  (-> (or (env :min-interval-answer)   "60") parse-long))
(def min-interval-comments (-> (or (env :min-inverval-comments) "60") parse-long))
(def min-interval-uploads  (-> (or (env :min-inverval-uploads)  "30") parse-long))
(def kp2-flash (-> (or (env :flash) "3") parse-long))
(def max-comments (-> (or (env :max-comments) "86400") parse-long))
(def max-uploads  (-> (or (env :max-uploads)  "86400") parse-long))

(defn- flash [user msg]
  (c/setex (format "kp2:%s:flash"  user) kp2-flash msg))

(defn- key- [what user]
  (format "kp2:%s:%s" what user))

(defn- key-comment [user]
  (key- "comment" user))

(defn- key-upload [user]
  (key- "upload" user))

(defn before-comment [user]
  (if-let [last-submission (c/get (key-comment user))]
    (do
      (flash user (format "しっかりコメント読むには%s秒は短いだろ。最終コメント時間 %s"
                          min-interval-comments
                          last-submission))
      false)
    true))

(defn after-comment [user]
  (let [key-interval (key-comment user)]
    (c/setex  key-interval min-interval-comments (local-time))))

(defn before-upload [user]
  (if-let [last-submission (c/get (key-upload user))]
    (do
      (flash user (format "%s秒で解ける？一題ずつ。自力で。%s"
                          min-interval-uploads
                          last-submission))
      false)
    true))

(defn after-upload [user]
  (let [key (key-upload user)]
    (c/setex key min-interval-uploads (local-time))))

(comment
  (before-comment "h")
  (c/get "kp2:h:flash")
  (after-comment "h")
  (before-upload "h")
  (c/get "kp2:h:flash")
  (c/ttl "kp2:h:flash")
  (after-upload "h")

  :rcf)
