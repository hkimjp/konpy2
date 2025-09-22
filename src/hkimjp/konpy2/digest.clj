(ns hkimjp.konpy2.digest
  (:require
   [clojure.string :as str]))

; https://groups.google.com/g/clojure/c/Kpf01CX_ClM
(defn- sha1 [s]
  (->>  (.getBytes s "UTF-8")
        (.digest (java.security.MessageDigest/getInstance "SHA1"))
        (java.math.BigInteger. 1)
        (format "%x")))

(defn- remove-line-comment
  [line]
  (str/replace line #"#.*" ""))

(defn- remove-docstrings
  [line]
  (let [comments (re-pattern "\"\"\".*?\"\"\"")]
    (str/replace line comments "")))

(defn remove-comments
  [s]
  (->> s
       str/split-lines
       (map remove-line-comment)
       (apply str)
       remove-docstrings))

(defn digest [s]
  (-> s
      remove-comments
      sha1
      (subs 0 7)))

; (digest "abc\ndef")
