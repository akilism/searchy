(ns vice-search.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :as ring-json]
            [ring.middleware.cors :as ring-cors]
            [ring.util.response :as rr]
            [vice-search.data.api-fetch :as api]
            [vice-search.cluster.management :as cluster]
            [vice-search.cluster.query :as query]
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
  (GET "/api-test/articles-all.json" []
       (let [reply (api/api-get-all :articles)]
         (rr/response {:reply reply})))
  (GET "/cluster-test/create.json" []
       (let [cluster-response (cluster/create-vice-index)]
         (rr/response {:result cluster-response})))
  (GET "/cluster-test/insert_articles.json" []
       (let [articles (api/api-get-all :articles)
             cluster-response (cluster/insert-articles articles)]
         (rr/response {:result cluster-response})))
  (GET "/search_articles.json" [q]
    (rr/response {:results (query/article-query q)}))
  (GET "/search_topics.json" [q]
    (rr/response {:results (query/topic-query q)}))
  (route/not-found "Not Found"))

(def app
  (-> app-routes
      (wrap-defaults site-defaults)
      (ring-cors/wrap-cors :access-control-allow-origin [#".*"]
                           :access-control-allow-methods [:get :post :put :delete])
      (ring-json/wrap-json-response)))
