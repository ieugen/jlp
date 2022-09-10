(ns com.netdava.jlp.web.routes.ui
  (:require
   [com.netdava.jlp.web.middleware.exception :as exception]
   [com.netdava.jlp.web.routes.utils :as utils]
   [com.netdava.jlp.web.htmx :refer [ui page] :as htmx]
   [integrant.core :as ig]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]))

(defn home [request]
  (let [{:keys [uri]} request
        clicked-path (str uri "/clicked")]
    (page
     [:head
      [:meta {:charset "UTF-8"}]
      [:title "Htmx + Kit"]
      [:script {:src "https://unpkg.com/htmx.org@1.7.0/dist/htmx.min.js" :defer true}]
      [:script {:src "https://unpkg.com/hyperscript.org@0.9.5" :defer true}]]
     [:body
      [:h1 "Welcome to Htmx + Kit module"]
      [:button {:hx-post clicked-path :hx-swap "outerHTML"} "Click me!"]])))

(defn clicked [request]
  (ui
   [:div "Congratulations! You just clicked the button!"]))

;; Routes
(defn ui-routes [_opts]
  [["" {:get home}]
   ["/" {:get home}]
   ["/clicked" {:post clicked}]])

(defn route-data [opts]
  (merge
   opts
   {:middleware 
    [;; Default middleware for ui
     ;; query-params & form-params
     parameters/parameters-middleware
     ;; encoding response body
     muuntaja/format-response-middleware
     ;; exception handling
     exception/wrap-exception]}))

(derive :reitit.routes/ui :reitit/routes)

(defmethod ig/init-key :reitit.routes/ui
  [_ {:keys [base-path]
      :or   {base-path ""}
      :as   opts}]
  [base-path (route-data opts) (ui-routes opts)])
