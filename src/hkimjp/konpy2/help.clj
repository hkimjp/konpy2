(ns hkimjp.konpy2.help
  (:require
   [hkimjp.konpy2.view :refer [page]]))

(defn help
  [_request]
  (page
   [:div.mx-4
    [:div.text-2xl.font-medium "Help"]
    [:div
     [:div [:span.font-bold "(admin)"]
      [:p.mx-4 "hkimura 専用。"]]]]))


