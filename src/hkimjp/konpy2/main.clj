(ns hkimjp.konpy2.main
  (:gen-class)
  (:require [hkimjp.konpy2.system :as system]))

(defn -main [& _args]
  (system/start-system))
