(ns hkimjp.konpy2.restrictions
  (:require
   [environ.core :refer [env]]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.konpy2.util :refer [local-time]]))

(def min-interval-comments (-> (or (env :min-interval-comments) "60") parse-long))
(def min-interval-uploads  (-> (or (env :min-interval-uploads)  "30") parse-long))
(def kp2-flash (-> (or (env :flash) "3") parse-long))
(def max-comments (-> (or (env :max-comments) "6") parse-long))
(def max-uploads  (-> (or (env :max-uploads)  "6") parse-long))

(defn- key- [what user]
  (format "kp2:%s:%s" what user))

(defn- key-comment [user]
  (key- "comment" user))

(defn- key-upload [user]
  (key- "upload" user))

(defn- key-comment-max [user]
  (key- "comment-max" user))

(defn- key-upload-max [user]
  (key- "upload-max" user))

(defn- uniq-name [s]
  (format "%s-%s" s (-> (random-uuid) str (subs 0 8))))

;;(uniq-name "hkimura")

(key-upload "hkimura")

(defn before-upload [user]
  (when-let [last-submission (c/get (key-upload user))]
    (throw (Exception.
            (format "アップロードは %s 秒以内にはできない。一題ずつ自力で。最終アップロード %s"
                    min-interval-uploads
                    last-submission))))
  (when (<= max-uploads (count (c/keys (str (key-upload user) "-*"))))
    (throw (Exception.
            (format "一日の最大アップロード数 %d を超えました。" max-uploads)))))

(defn after-upload [user]
  (let [lt (local-time)]
    (t/log! {:level :debug :data {:key (key-upload user) :min-inverval-uploads min-interval-uploads}})
    (c/setex (key-upload user) min-interval-uploads lt)
    (c/setex (uniq-name (key-upload user)) (* 24 60 60) lt)))

;; FIXME:
;; almost same with before-upload
(defn before-comment [user]
  (when-let [last-submission (c/get (key-comment user))]
    (throw (Exception.
            (format "しっかりコメント読み書きするのに %s 秒は短いだろ。最終コメント時間 %s"
                    min-interval-comments
                    last-submission))))
  (when (<= max-comments (count (c/keys (str (key-comment user) "-*"))))
    (throw (Exception.
            (format "一日の最大コメント数 %d を超えました。" max-comments)))))

;; FIXME:
;; almost same with after-upload
(defn after-comment [user]
  (let [lt (local-time)]
    (c/setex (key-comment user) min-interval-comments lt)
    (c/setex (uniq-name (key-comment user)) (* 24 60 60) lt)))

(comment
  (before-comment "h")
  (c/get "kp2:h:flash")
  (after-comment "h")
  (before-upload "h")
  (c/get "kp2:h:flash")
  (c/ttl "kp2:h:flash")
  (after-upload "h")

  :rcf)
