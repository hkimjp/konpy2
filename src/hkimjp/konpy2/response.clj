(ns hkimjp.konpy2.response
  (:require
   [hiccup2.core :as h]
   [ring.util.response :as resp]
   [taoensso.telemere :as t]))

(def version "0.2.9-SNAPSHOT")

(def ^:private menu "text-xl font-medium text-white px-1 hover:bg-red-500")

(defn navbar []
  [:div.flex.bg-red-600.items-baseline.gap-x-4
   [:div.text-2xl.font-medium.text-white "KONPY2"]
   [:div {:class menu} [:a {:href "/k/tasks"}  "tasks"]]
   [:div {:class menu} [:a {:href "/k/scores"} "scores"]]
   [:div {:class menu} [:a {:href "/k/stocks"} "stocks"]]
   [:div {:class menu} [:a {:href "/logout"}   "logout"]]
   [:div {:class menu} [:a {:href "/help"}     "HELP"]]
   [:div {:class menu} [:a {:href "/admin/"}   "(admin)"]]])

(def footer
  [:div.text-base
   [:hr]
   "hkimura " version])

(defn- base
  [content]
  [:html {:lang "en"}
   [:head
    [:meta {:charset "UTF-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1"}]
    [:link {:type "text/css"
            :rel  "stylesheet"
            :href "/assets/css/output.css"}]
    [:link {:rel "icon"
            :href "/favicon.ico"}]
    [:script {:type "text/javascript"
              :src  "/assets/js/htmx.min.js"
              :defer true}]
    [:title "KONPY2"]]
   [:body {:hx-boost "true"}
    [:div
     (navbar)
     content
     footer]]])

(defn page
  [content]
  (t/log! :info (str "page"))
  (-> (str (h/html (h/raw "<!DOCTYPE html>") (base content)))
      resp/response
      (resp/header "Content-Type" "text/html")))

;; htmx requires html response.
(defn hx [content]
  (t/log! {:level :debug :id "hx" :data content})
  (-> content
      h/html
      str
      resp/response
      (resp/content-type "text/html")))

