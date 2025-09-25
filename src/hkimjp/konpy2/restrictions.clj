(ns hkimjp.konpy2.restrictions
  (:require
   [environ.core :refer [env]]
   [java-time.api :as jt]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]))

; name                       expire        value
; kp2:upload:<user>          min-interval  last submission time
; kp2:upload:<user>:<time>   24            local-time of uploading
; kp2:comment:<user>         min-interval  last comment time
; kp2:comment:<user>:<time>  24 hour       local-time of commenting

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

(def kp2-flash (-> (or (env :flash) "3") parse-long))

(def must-read-before-upload
  "min number of reading comments before uploading one's answer"
  (-> (or (env :must-read-before-upload)  "3") parse-long))

(def must-write-before-upload
  "min number of reading comments before uploading one's answer"
  (-> (or (env :must-write-before-upload)  "1") parse-long))

;-----------------------

;; keys will be exipred
(defn- key- [what user]
  (format "kp2:%s:%s" what user))

(defn- key-comment [user]
  (key- "comment" user))

(defn- key-comment-time [user]
  (str (key-comment user) ":" (local-time)))

(defn- key-upload [user]
  (key- "upload" user))

(defn- key-upload-time [user]
  (str (key-upload user) ":" (local-time)))

;; keys not expired
; must be export. must be public.
(defn key-comment-read [user]
  (str (key-comment user) ":read"))

(defn- key-comment-write [user]
  (str (key-comment user) ":write"))

;-------------------------

(defn before-upload [user]
  (when (<= max-uploads (count (c/keys (str (key-upload user) "-*"))))
    (throw (Exception.
            (format "一日の最大アップロード数 %d を超えました。" max-uploads))))
  (when-let [last-submission (c/get (key-upload user))]
    (throw (Exception.
            (format "アップロードは %s 秒以内にはできない。一題ずつ自力で。最終アップロード %s"
                    min-interval-uploads
                    last-submission))))
  ;FIXME: complex.
  (when (and (pos? (count (c/keys (str (key-upload user) ":*"))))
             (<= (-> (c/get (key-comment-read user)) parse-long)
                 must-read-before-upload)
             (<= (-> (c/get (key-comment-write user)) parse-long)
                 must-write-before-upload))
    (throw (Exception.
            (format "回答アップロードの前にコメントを %d 以上読むか、%d 以上書かないとだめ。"
                    must-read-before-upload
                    must-write-before-upload)))))

(defn before-comment [user]
  (when-let [last-submission (c/get (key-comment user))]
    (throw (Exception.
            (format "しっかりコメント読み書きするのに %s 秒は短いだろ。最終コメント時間 %s"
                    min-interval-comments
                    last-submission))))
  (when (<= max-comments (count (c/keys (str (key-comment user) "-*"))))
    (throw (Exception.
            (format "一日の最大コメント数 %d を超えました。" max-comments)))))

(defn after-upload [user]
  (let [lt (local-time)]
    (t/log! {:level :debug :data {:key (key-upload user) :min-inverval-uploads min-interval-uploads}})
    (c/setex (key-upload user) min-interval-uploads lt)
    (c/setex (key-upload-time user) (* 24 60 60) lt)
    (c/set (key-comment-read user) 0)
    (c/set (key-comment-write user) 0)))

(defn after-comment [user]
  (let [lt (local-time)]
    (c/setex (key-comment user) min-interval-comments lt)
    (c/setex (key-comment-time user) (* 24 60 60) lt)
    (c/incr (key-comment-write user))))
