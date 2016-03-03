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

(defmethod insert [type cluster index-name doc key]
  (let [id (get-in doc [key])]
    (esd/put cluster index-name (name type) id doc)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [cluster (esr/connect "http://127.0.0.1:9200")
        index-name "test0"
        mapping-types {"person" {:properties {:username   {:type "string" :store "yes"}
                                              :first-name {:type "string" :store "yes"}
                                              :last-name  {:type "string"}
                                              :age        {:type "integer"}
                                              :title      {:type "string" :analyzer "snowball"}
                                              :planet     {:type "string"}
                                              :biography  {:type "string" :analyzer "snowball" :term_vector "with_positions_offsets"}}}}
        doc {:username "happyjoe" :first-name "Joe" :last-name "Smith" :age 30 :title "Teh Boss" :planet "Earth" :biography "N/A"}]
    (create-index cluster index-name {:settings {"number_of_shards" 1} :mappings mapping-types})
    (insert :person cluster index-name doc :username)))
