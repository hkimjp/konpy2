(ns hkimjp.konpy2.restrictions
  (:require
   [environ.core :refer [env]]
   [hkimjp.carmine :as c]
   [hkimjp.konpy2.util :refer [local-time]]))

(def min-interval-comments (-> (or (env :min-inverval-comments) "60") parse-long))
(def min-interval-uploads  (-> (or (env :min-inverval-uploads)  "30") parse-long))
(def kp2-flash (-> (or (env :flash) "3") parse-long))
(def max-comments (-> (or (env :max-comments) "6") parse-long))
(def max-uploads  (-> (or (env :max-uploads)  "6") parse-long))

(defn- flash [user msg]
  (c/setex (format "kp2:%s:flash"  user) kp2-flash msg))

(defn- key- [what user]
  (format "kp2:%s:%s" what user))

(defn- key-comment [user]
  (key- "comment" user))

(defn- key-upload [user]
  (key- "upload" user))

(defn before-upload [user]
  (when-let [last-submission (c/get (key-upload user))]
    (throw (Exception.
            (format "アップロードは %s 秒以内にはできない。一題ずつ自力で。最終アップロード %s"
                    min-interval-uploads
                    last-submission)))))

(defn after-upload [user]
  (c/setex (key-upload user) min-interval-uploads (local-time)))

(defn before-comment [user]
  (when-let [last-submission (c/get (key-comment user))]
    (throw (Exception.
            (format "しっかりコメント読み書きするに %s 秒は短いだろ。最終コメント時間 %s"
                    min-interval-comments
                    last-submission)))))

(defn after-comment [user]
  (c/setex (key-comment user) min-interval-comments (local-time)))

(comment
  (before-comment "h")
  (c/get "kp2:h:flash")
  (after-comment "h")
  (before-upload "h")
  (c/get "kp2:h:flash")
  (c/ttl "kp2:h:flash")
  (after-upload "h")

  :rcf)
