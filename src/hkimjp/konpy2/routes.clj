(ns hkimjp.konpy2.routes
  (:require
   [clj-simple-stats.core :refer [wrap-stats]]
   [reitit.ring :as rr]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [hkimjp.konpy2.middleware :as m]
   [hkimjp.konpy2.admin :as admin]
   [hkimjp.konpy2.answers :as answers]
   [hkimjp.konpy2.comments :as comments]
   [hkimjp.konpy2.help :refer [help]]
   [hkimjp.konpy2.login :refer [login login! logout!]]
   [hkimjp.konpy2.scores :as scores]
   [hkimjp.konpy2.stocks :as stocks]
   [hkimjp.konpy2.tasks :as tasks]))

(def routes
  [["/" {:middleware []}
    ["" {:get login :post login!}]
    ["dl/:eid" {:get answers/dl}]
    ["logout" logout!]
    ["help"   {:get help}]
    #_["stats" {:get {:middleware [m/wrap-admin wrap-stats]
                      :handler (fn [_] {:status 200 :body ""})}}]]
   ["/admin/" {:middleware [m/wrap-admin]}
    [""           {:get admin/admin}]
    ["new"        {:get admin/new  :post admin/upsert!}]
    ["update/:e"  {:get admin/edit :post admin/upsert!}]
    ["eid"        {:post admin/eid}]
    ["delete"     {:post admin/delete!}]]
   ["/k/" {:middleware [m/wrap-users]}
    ["answer"       {:post answers/answer!}]
    ["answer/:e/:p" {:get  answers/hx-answer}]
    ["comment"      {:post comments/comment!}]
    ["comment/:e"   {:get  comments/hx-comment}]
    ["hx-answers"   {:get tasks/hx-answers}]
    ["hx-comments"  {:get tasks/hx-comments}]
    ["hx-logins"    {:get tasks/hx-logins}]
    ["problem/:e"   {:get tasks/problem}]
    ["score/:e"     {:get scores/hx-show}]
    ["scores"       {:get scores/scores}]
    ["scores/peep"  {:post scores/hx-peep}]
    ["stock/:e"     {:get stocks/stock}]
    ["stocks"       {:get stocks/stocks :post stocks/stocks!}]
    ["tasks"        {:get tasks/tasks}]
    #_["tasks-all"    {:get tasks/tasks-all}]]])

(def root-handler
  (rr/ring-handler
   (rr/router routes)
   (rr/routes
    (rr/create-resource-handler {:path "/"})
    (rr/create-default-handler
     {:not-found
      (constantly {:status 404
                   :headers {"Content-Type" "text/html"}
                   :body "<h1>ERROR</h1><p>not found</p>"})
      :method-not-allowed
      (constantly {:status 405
                   :body "not allowed"})
      :not-acceptable
      (constantly {:status 406
                   :body "not acceptable"})}))
   {:middleware [wrap-stats
                 [wrap-defaults site-defaults]]}))

; (root-handler {:uri "/admin/eid" :request-method "post"})
; site-defaults
