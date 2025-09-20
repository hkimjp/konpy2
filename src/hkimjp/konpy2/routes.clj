(ns hkimjp.konpy2.routes
  (:require
   [reitit.ring :as rr]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [taoensso.telemere :as t]
   [hkimjp.konpy2.middleware :as m]
   [hkimjp.konpy2.admin :as admin]
   [hkimjp.konpy2.answers :as answers]
   [hkimjp.konpy2.comments :as comments]
   [hkimjp.konpy2.help :refer [help]]
   [hkimjp.konpy2.login :refer [login login! logout!]]
   [hkimjp.konpy2.scores :as scores]
   [hkimjp.konpy2.stocks :as stocks]
   [hkimjp.konpy2.tasks :as tasks]))

(defn routes []
  [["/" {:middleware []}
    ["" {:get login :post login!}]
    ["logout" logout!]]
   ["/help"   {:get help}]
   ["/admin/" {:middleware [m/wrap-admin]}
    [""           {:get admin/problems}]
    ["problems"   {:get admin/problems}]
    ["new"        {:get admin/new  :post admin/upsert!}]
    ["update/:e"  {:get admin/edit :post admin/upsert!}]]
   ["/k/" {:middleware [m/wrap-users]}
    ["tasks"        {:get tasks/konpy}]
    ["problem/:e"   {:get tasks/problem}]
    ["hx-answers"   {:get tasks/hx-answers}]
    ["hx-comments"  {:get tasks/hx-comments}]
    ["answer"       {:post answers/answer!}]
    ["answer/:e/:p" {:get  answers/hx-answer}]
    ["comment"      {:post comments/comment!}]
    ["comment/:e"   {:get  comments/hx-comment}]
    ["scores"       {:get scores/scores}]
    ["stocks"       {:get stocks/stocks :post stocks/stocks!}]
    ["stock/:e"     {:get stocks/stock}]]])

(defn root-handler
  [request]
  (t/log! :info (str (:request-method request) " - " (:uri request)))
  (let [handler
        (rr/ring-handler
         (rr/router (routes))
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
         {:middleware [[wrap-defaults site-defaults]]})]
    (handler request)))

