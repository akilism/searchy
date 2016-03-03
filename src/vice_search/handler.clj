(ns vice-search.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as rr]))

(defroutes app-routes
  (GET "/" [] ":8(")
  (GET "/search.json" [q]
    (rr/response {:results [{:id "test" :val q}]}))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (ring-json/wrap-json-response)))
; (def app
;   (wrap-defaults app-routes site-defaults))
