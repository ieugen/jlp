(ns com.netdava.jlp.web.handler
  (:require
   [com.netdava.jlp.web.middleware.core :as middleware]
   [integrant.core :as ig]
   [reitit.ring :as ring]
   [reitit.swagger-ui :as swagger-ui]))

(defmethod ig/init-key :handler/ring
  [_ {:keys [router api-path] :as opts}]
  (ring/ring-handler
   router
   (ring/routes
    (ring/create-resource-handler {:path "/"})
    (when (some? api-path)
      (swagger-ui/create-swagger-ui-handler {:path api-path
                                             :url  (str api-path "/swagger.json")}))
    (ring/routes
      ;; Handle trailing slash in routes - add it + redirect to it
      ;; https://github.com/metosin/reitit/blob/master/doc/ring/slash_handler.md
     (ring/redirect-trailing-slash-handler)
     (ring/create-default-handler
      {:not-found
       (constantly {:status 404, :body "Page not found"})
       :method-not-allowed
       (constantly {:status 405, :body "Not allowed"})
       :not-acceptable
       (constantly {:status 406, :body "Not acceptable"})})))
   {:middleware [(middleware/wrap-base opts)]}))

(defmethod ig/init-key :router/routes
  [_ {:keys [routes]}]
  (apply conj [] routes))

(defmethod ig/init-key :router/core
  [_ {:keys [routes] :as opts}]
  (ring/router ["" opts routes]))
