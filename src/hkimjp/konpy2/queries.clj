(ns hkimjp.konpy2.queries
  (:require
   [hkimjp.datascript :as ds]))

(def ^:private answers-q
  '[:find ?e
    :in $ ?author
    :where
    [?e :author ?author]
    [?e :answer/status "yes"]])

(def ^:private sent-q
  '[:find ?e
    :in $ ?author
    :where
    [?e :author ?author]
    [?e :comment/status "yes"]])

(def ^:private received-q
  '[:find ?e ?pt
    :in $ ?author
    :where
    [?e :to ?a]
    [?a :author ?author]
    [?e :pt ?pt]
    [?e :comment/status "yes"]])

(defn entries [eid]
  (ds/pl eid))

(defn answers [user]
  (ds/qq answers-q user))

(defn sent [user]
  (ds/qq sent-q user))

(defn received [user]
  (ds/qq received-q user))
