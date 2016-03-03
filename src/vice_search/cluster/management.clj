(ns vice-search.cluster.management
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [clojurewerkz.elastisch.rest.document :as esd]))

(def not-exists? (complement esi/exists?))

(defn create-index
  "Create an index on a given cluster with a given name and optionally
  a map of options with the keys :settings and :mappings that are any
  custom settings or mappings for the index."
  ([cluster index-name]
   (when (not-exists? cluster index-name)
     (esi/create cluster index-name)))
  ([cluster index-name options]
   (let [{[settings mappings] :keys} options]
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
  (let [cluster (esr/connect "http://127.0.0.1:9200")
        index-name "vice"
        mapping-types {"article" {:properties {:title {:type "string" :store "yes"}
                                               :id {:type "string" :store "yes"}
                                               :body {:type "string"}
                                               :slug {:type "string"}
                                               :title-analyze {:type "string" :analyzer "snowball"}}}}]
    (create-index cluster index-name)))

(defn insert-articles [articles]
  (let [cluster (esr/connect "http://127.0.0.1:9200")
        index-name "vice"]
    (map #(insert :article cluster index-name (assoc % :title-analyze (:title %)) :id) articles)))
