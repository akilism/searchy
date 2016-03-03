(ns vice-search.cluster.query
	(:require [clojure.pprint :as pp]
   					[clojurewerkz.elastisch.rest :as esr]
						[clojurewerkz.elastisch.rest.document :as esd]
						[clojurewerkz.elastisch.query :as es-qry]
						[clojurewerkz.elastisch.rest.response :as es-res]))

(defmulti build-query (fn [type & _] type))

(defmethod build-query :simple
  [_ field query-term]
  {:query_string {:query query-term}})

(defmethod build-query :term
  [_ field query-term]
  {:term {field query-term}})

(defn simple-query
	[type field query-term]
	(let [cluster (esr/connect "http://127.0.0.1:9200")
				index-name "vice"
				response (esd/search cluster index-name (name type) :query (build-query :simple field query-term))
				total-hits (es-res/total-hits response)
				hits (es-res/hits-from response)]
		(println "Query: " query-term)
  	(println "Total Hits: " total-hits)
		hits))

(defn term-query
	[type field query-term]
	(let [cluster (esr/connect "http://127.0.0.1:9200")
				index-name "vice"
				response (esd/search cluster index-name (name type) :query (build-query :term field query-term))
				total-hits (es-res/total-hits response)
				hits (es-res/hits-from response)]
		(println "Query: " query-term)
  	(println "Total Hits: " total-hits)
		hits))

(defn article-query
  [query-term]
  (simple-query :article :title query-term))

(defn topic-query
  [query-term]
  (term-query :article :topics.name query-term))
