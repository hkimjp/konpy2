(ns hkimjp.konpy2.routes
  (:require
   [reitit.ring :as rr]
   [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
   [taoensso.telemere :as t]
   [hkimjp.konpy2.middleware :as m]
   [hkimjp.konpy2.admin :as admin]
   [hkimjp.konpy2.help :refer [help]]
   [hkimjp.konpy2.login :refer [login login! logout!]]))

(defn routes
  []
  [["/"         {:get login :post login!}]
   ["/logout"   logout!]
   ["/help"     {:get help}]
   ["/admin"    {:middleware [m/wrap-admin]}
    [""           {:get admin/admin}]]])

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
