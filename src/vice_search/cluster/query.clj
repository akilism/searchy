(ns vice-search.cluster.query
	(:require [clojure.pprint :as pp]
   					[clojurewerkz.elastisch.rest :as esr]
						[clojurewerkz.elastisch.rest.document :as esd]
						[clojurewerkz.elastisch.query :as es-qry]
						[clojurewerkz.elastisch.rest.response :as es-res]))

(def cluster-url "http://127.0.0.1:9200")
(def index-name "vice")

(defmulti build-query (fn [type & _] type))

(defmethod build-query :simple
  [_ field query-term]
  {:query_string {:query query-term}})

(defmethod build-query :term
  [_ field query-term]
  {:term {field query-term}})

(defn query
	[query-type type field query-term]
	(let [cluster (esr/connect cluster-url)
				response (esd/search cluster index-name (name type) :query (build-query query-type field query-term) :size 100)
				total-hits (es-res/total-hits response)
				hits (es-res/hits-from response)]
		(println "Query: " query-term " Field: " (name field))
  	(println "Total Hits: " total-hits)
		hits))

(defn article-query
  "FIXME Make this search only articles."
  [query-term]
  (query :simple :items :title query-term))

(defn contributor-query
  [query-term]
  (query :term :items :contributions.contributor.id query-term))

(defn item-query
  [query-term]
  (query :simple :items :title query-term))

(defn topic-query
  [query-term]
  (query :term :items :topics.id query-term))
