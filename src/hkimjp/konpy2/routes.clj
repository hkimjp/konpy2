(ns hkimjp.konpy2.routes
  (:require
   [reitit.ring :as rr]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
   [taoensso.telemere :as t]
   [hkimjp.konpy2.middleware :as m]
   [hkimjp.konpy2.admin :as admin]
   [hkimjp.konpy2.help :refer [help]]
   [hkimjp.konpy2.login :refer [login login! logout!]]
   [hkimjp.konpy2.tasks :as tasks]
   [hkimjp.konpy2.answers :as answers]
   [hkimjp.konpy2.response :refer [page hx]]))

(defn dummy [request]
  (page [:div (:request-method request) " " (:uri request)]))

(defn routes
  []
  [["/" {:middleware [[wrap-defaults site-defaults] m/wrap-users]}
    ["" {:get tasks/konpy}]
    ["problem/:e" {:get tasks/problem}]]
   ["/login" {:middleware [[wrap-defaults site-defaults]]}
    ["" {:get login :post login!}]]
   ["/logout" logout!]
   ["/scores" {:middleware [[wrap-defaults site-defaults] m/wrap-users]}
    ["" {:get dummy}]]
   ["/stocks" {:middleware [[wrap-defaults site-defaults] m/wrap-users]}
    ["" {:get dummy}]]
   ["/help" {:get help}]
   ["/admin" {:middleware [[wrap-defaults site-defaults] m/wrap-admin]}
    ["" {:get admin/admin}]
    ["/problems" {:get admin/problems}]
    ["/new"  {:get admin/new :post admin/upsert!}]
    ["/update/:e" {:get admin/edit :post admin/upsert!}]
    ["/delete/:e" {:delete admin/delete!}]]
   ["/answers" {:middleware [[wrap-defaults site-defaults] m/wrap-users]}
    "/:e" {:get get-answers}
    "/" {:post post-answer}]
   ["/comments" {:middleware [[wrap-defaults site-defaults] m/wrap-users]}]
   ["/hx" {:middleware [[wrap-defaults api-defaults]]}
    ["/hello" hx]
    ["/answers/:e" get-answers]
    ["/answer" {:post post-answer}]]])

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
                         :body "not found"})
            :method-not-allowed
            (constantly {:status 405
                         :body "not allowed"})
            :not-acceptable
            (constantly {:status 406
                         :body "not acceptable"})}))
         {:middleware []})]
    (handler request)))
