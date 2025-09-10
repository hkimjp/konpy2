(ns hkimjp.konpy2.core
  (:gen-class)
  (:require [hkimjp.konpy2.system :as system]))

(defn -main [& _args]
  (system/start-system))


