(ns hkimjp.konpy2.restrictions
  (:require
   [environ.core :refer [env]]
   [java-time.api :as jt]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]))

; name                            expire        value
; kp2:upload:<user>               min-interval  last submission time
; kp2:uploads:<user>:<local-date> never           local-time of uploading
; kp2:comment:<user>              min-interval  last comment time
; kp2:comments:<user>:<local-date>  never       local-time of commenting

(defn- local-time []
  (jt/format "HHmmss" (jt/local-time)))

(def min-interval-comments
  "minimum interval between comments"
  (-> (or (env :min-interval-comments) "60") parse-long))

(def min-interval-uploads
  "minimum interval between uploads"
  (-> (or (env :min-interval-uploads)  "60") parse-long))

(def max-comments
  "max number of comments in a day"
  (-> (or (env :max-comments) "6") parse-long))

(def max-uploads
  "max number of uploads in a day"
  (-> (or (env :max-uploads)  "6") parse-long))

(def kp2-flash (-> (or (env :flash) "1") parse-long))

(def must-read-before-upload
  "min number of reading comments before uploading one's answer"
  (-> (or (env :must-read-before-upload)  "3") parse-long))

(def must-write-before-upload
  "min number of reading comments before uploading one's answer"
  (-> (or (env :must-write-before-upload)  "1") parse-long))

;-----------------------

(defn- key-comment [user]
  (format "kp2:%s:comment" user))

(defn- key-comments [user]
  (format "kp2:%s:comments" user))

(defn- key-upload [user]
  (format "kp2:%s:upload" user))

(defn- key-uploads [user]
  (format "kp2:%s:uploads" user))

;; reset to zero after an upload.
(defn key-comment-read [user]
  (format "kp2:%s:read" user))

(defn- key-comment-write [user]
  (format "kp2:%s:write" user))

;-------------------------

(defn before-upload [user]
  (when (<= max-uploads (c/llen (key-uploads user)))
    (throw (Exception.
            (format "一日の最大アップロード数 %d を超えました。" max-uploads))))
  (when-let [last-submission (c/get (key-upload user))]
    (throw (Exception.
            (format "アップロードは %d 秒以内にはできない。一題ずつ自力でな。最終アップロード %s"
                    min-interval-uploads last-submission))))
  ;FIXME:
  (when (and (pos? (c/llen (key-uploads user))) ;
             (< (-> (c/get (key-comment-read user)) parse-long)
                must-read-before-upload)
             (< (-> (c/get (key-comment-write user)) parse-long)
                must-write-before-upload))
    (throw (Exception.
            (format "回答アップロードの前にコメントを %d 以上読むか、%d 以上書く必要ある。"
                    must-read-before-upload must-write-before-upload)))))

(defn before-comment [user]
  (when-let [last-comment-at (c/get (key-comment user))]
    (throw (Exception.
            (format "しっかりコメント読み書きするのに %d 秒は短いだろ。最終コメント時間 %s"
                    min-interval-comments
                    last-comment-at))))
  (when (<= max-comments (c/llen (key-comments user)))
    (throw (Exception.
            (format "一日の最大コメント数 %d を超えました。" max-comments)))))

(defn after-upload [user]
  (let [lt (local-time)]
    (t/log! {:level :debug :data {:key (key-upload user) :min-inverval-uploads min-interval-uploads}})
    (c/setex (key-upload user) min-interval-uploads lt)
    (c/lpush (key-uploads user) lt)
    (c/expire (key-uploads user) (* 24 60 60))
    (c/set (key-comment-read user) 0)
    (c/set (key-comment-write user) 0)))

(defn after-comment [user]
  (let [lt (local-time)]
    (c/setex (key-comment user) min-interval-comments lt)
    (c/lpush (key-comments user) lt)
    (c/expire (key-comments user) (* 24 60 60))
    (c/incr (key-comment-write user))))
