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

(defn query
	[query-type type field query-term]
	(let [cluster (esr/connect "http://127.0.0.1:9200")
				index-name "vice"
				response (esd/search cluster index-name (name type) :query (build-query query-type field query-term))
				total-hits (es-res/total-hits response)
				hits (es-res/hits-from response)]
		(println "Query: " query-term " Field: " (name field))
  	(println "Total Hits: " total-hits)
		hits))

(defn article-query
  [query-term]
  (query :simple :articles :title query-term))

(defn topic-query
  [query-term]
  (query :term :articles :topics.id query-term))

(defn contributor-query
  [query-term]
  (query :term :articles :contributions.contributor.id query-term))
