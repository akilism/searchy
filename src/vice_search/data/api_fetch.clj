(ns vice-search.data.api-fetch
  (:require [clj-http.client :as http]))

(def api-url-dev "https://dev-api.viceops.net/api")
(def api-url-prod "")

(def api-ver "v1")

(def locale :en_us)

(def type :articles)

(def default-params {:platform "web" :client "vice" :per_page 25})

(defn api-url [type]
  (str api-url-dev "/" api-ver "/" (name locale) "/" type))

(defn api-get
  ([type]
   (http/get (api-url (name type)) {:query-params default-params
                                    :accept :json})))
