(ns vice-search.cluster.management
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]))

(def cluster-url "http://127.0.0.1:9200")
(def index-name "vice")

(def not-exists? (complement esi/exists?))

(defn create-index
  "Create an index on a given cluster with a given name and optionally
  a map of options with the keys :settings and :mappings that are any
  custom settings or mappings for the index."
  ([cluster index-name]
   (when (not-exists? cluster index-name)
     (esi/create cluster index-name)))
  ([cluster index-name options]
   (let [{:keys [mappings settings]} options]
     (println mappings)
     (when (not-exists? cluster index-name)
       (cond
         (and settings mappings) (esi/create cluster index-name :settings settings :mappings mappings)
         settings (esi/create cluster index-name :settings settings)
         mappings (esi/create cluster index-name :mappings mappings)
         :else (esi/create cluster index-name))))))

(defn insert [type cluster index-name doc key]
  "Inset a document into an index on a cluster.
   type is the mapping type as a keyword.
   doc is the document to insert.
   key is the field in the map to use as a unique identifier from the document."
  (let [id (get-in doc [key])]
    (println "Inserting: " (name type) ": " (:id doc))
    (esd/put cluster index-name (name type) id doc)))

(defn create-vice-index
  []
  (let [cluster (esr/connect cluster-url)
        mapping-types {"items" {:properties {:body {:type "string"}
          :channel {:properties {:id {:type "string" :index "not_analyzed"}
            :slug {:type "string" :index "not_analyzed"}}}
          :contributions {:properties {:id {:type "string" :index "not_analyzed"}
            :contributor {:properties {:id {:type "string" :index "not_analyzed"}
              :first_name {:type "string" :index "not_analyzed"}
              :last_name {:type "string" :index "not_analyzed"}
              :full_name {:type "string"}
              :twitter_username {:type "string" :index "not_analyzed"}
              :public_url {:type "string" :index "not_analyzed"}
              :thumbnail_url {:type "string" :index "not_analyzed"}
    					:thumbnail_url_1_1 {:type "string" :index "not_analyzed"}
    					:thumbnail_url_2_3 {:type "string" :index "not_analyzed"}
    					:thumbnail_url_7_10 {:type "string" :index "not_analyzed"}
    					:thumbnail_url_10_3 {:type "string" :index "not_analyzed"}
    					:thumbnail_url_10_4 {:type "string" :index "not_analyzed"}
    					:thumbnail_url_16_9 {:type "string" :index "not_analyzed"}
              :slug {:type "string" :index "not_analyzed"}}}}}
          :dek {:type "string"}
          :episode {:properties {:id {:type "string" :index "not_analyzed"}
            :season {:properties {:id {:type "string" :index "not_analyzed"}
              :show {:properties {:id {:type "string" :index "not_analyzed"}
                :original_id {:type "string" :index "not_analyzed"}
                :primary_topic {:properties {:id {:type "string" :index "not_analyzed"}
                  :name {:type "string"}
                  :slug {:type "string" :index "not_analyzed"}}}
                :slug {:type "string" :index "not_analyzed"}
                :thumbnail_url {:type "string" :index "not_analyzed"}
                :thumbnail_url_1_1 {:type "string" :index "not_analyzed"}
                :thumbnail_url_2_3 {:type "string" :index "not_analyzed"}
                :thumbnail_url_7_10 {:type "string" :index "not_analyzed"}
                :thumbnail_url_10_3 {:type "string" :index "not_analyzed"}
                :thumbnail_url_10_4 {:type "string" :index "not_analyzed"}
                :thumbnail_url_16_9 {:type "string" :index "not_analyzed"}
                :topics {:properties {:id {:type "string" :index "not_analyzed"}
                  :name {:type "string"}
                  :slug {:type "string" :index "not_analyzed"}}}
                :url {:type "string" :index "not_analyzed"}}}}}}}
          :id {:type "string" :index "not_analyzed"}
          :locale {:type "string" :index "not_analyzed"}
          :primary_topic {:properties {:id {:type "string" :index "not_analyzed"}
            :name {:type "string"}
            :slug {:type "string" :index "not_analyzed"}}}
          :rating {:type "string" :index "not_analyzed"}
          :slug {:type "string" :index "not_analyzed"}
          :summary {:type "string"}
          :topics {:properties {:id {:type "string" :index "not_analyzed"}
            :name {:type "string"}
            :slug {:type "string" :index "not_analyzed"}}}
          :title {:type "string"}
          :title-analyze {:type "string" :analyzer "snowball"}
          :thumbnail_url {:type "string" :index "not_analyzed"}
          :thumbnail_url_1_1 {:type "string" :index "not_analyzed"}
          :thumbnail_url_2_3 {:type "string" :index "not_analyzed"}
          :thumbnail_url_7_10 {:type "string" :index "not_analyzed"}
          :thumbnail_url_10_3 {:type "string" :index "not_analyzed"}
          :thumbnail_url_10_4 {:type "string" :index "not_analyzed"}
          :thumbnail_url_16_9 {:type "string" :index "not_analyzed"}
          :url {:type "string" :index "not_analyzed"}
          :vms_id {:type "string" :index "not_analyzed"}}}}]
    (create-index cluster index-name {:mappings mapping-types})))

(defn insert-items
  [item-type items]
  (let [cluster (esr/connect cluster-url)]
    (map
      #(insert :items
         cluster
         index-name
         (assoc % :title-analyze (:title %) :type (name item-type))
         :id)
      items)))
