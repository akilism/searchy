(ns vice-search.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as rr]
            [vice-search.data.api-fetch :as api]
            [vice-search.cluster.management :as cluster]
            [cheshire.core :as json]))

(defn api-response-body [raw-response with-keywords]
  (let [api-response-body (:body raw-response)
        reply (json/parse-string api-response-body with-keywords)]
    reply))

(defroutes app-routes
  (GET "/" [] ":8(")
  (GET "/api-test/articles.json" []
       (let [reply (api-response-body (api/api-get :articles) false)]
         (rr/response {:reply reply})))
  (GET "/cluster-test/create.json" []
       (let [cluster-response (cluster/create-vice-index)]
         (rr/response {:result cluster-response})))
  (GET "/cluster-test/insert_articles.json" []
       (let [api-response (api-response-body (api/api-get :articles) true)
             cluster-response (cluster/insert-articles api-response)]
         (rr/response {:result cluster-response})))
  (GET "/search.json" [q]
    (rr/response {:results [{:id "test" :val q}]}))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (ring-json/wrap-json-response)))
