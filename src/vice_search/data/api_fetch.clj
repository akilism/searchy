(ns vice-search.data.api-fetch
  (:require [clj-http.client :as http]
            [cheshire.core :as json]))

(def api-url-dev "https://dev-api.viceops.net/api")
(def api-url-prod "")

(def api-ver "v1")

(def locale :en_us)

(def request-type :articles)

(def default-params {:platform "web" :client "vice" :per_page 25})

(defn api-url [request-type]
  (str api-url-dev "/" api-ver "/" (name locale) "/" request-type))

(defn api-get
  ([request-type]
   (http/get (api-url (name request-type))
             					{:query-params default-params
                       :accept :json}))
  ([request-type custom-params]
   (http/get (api-url (name request-type))
             {:query-params (merge default-params custom-params)
              :accept :json})))

(defn api-body
  [response]
  (json/parse-string (:body response) true))

 (defn api-get-all
   [request-type]
   (let [response (http/get (api-url (name request-type)) {:query-params default-params :accept :json})
         headers (:headers response)
         total-count (Integer/parseInt (get-in headers ["X-Total-Count"]))
         page (Integer/parseInt (get-in headers ["X-Page"]))
         per-page (Integer/parseInt (get-in headers ["X-Per-Page"]))
         total-pages (Math/ceil (double (/ total-count per-page)))]
     (loop [pages (- total-pages 1)
            page 2
            body (api-body response)]
       ; (println "----------------")
       ; (println "total count: " total-count)
       ; (println "total pages: " total-pages)
       ; (println "per-page count: " per-page)
       ; (println "total pages: " pages)
       ; (println "current page: " page)
       ; (println "")
       (if (>= 0 pages)
         body
         (recur (dec pages) (inc page) (into body (api-body (api-get request-type {:page page}))))))))
