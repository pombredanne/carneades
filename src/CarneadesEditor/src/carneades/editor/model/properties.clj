;;; Copyright © 2010 Fraunhofer Gesellschaft 
;;; Licensed under the EUPL V.1.1

(ns carneades.editor.model.properties
  (:use clojure.contrib.def)
  (:require carneades.config.reader))

(defvar *properties-path* (str (System/getProperty "user.home")
                               java.io.File/separator
                               ".carneades.properties"))

(defvar *default-values*
  (into (sorted-map)
        {"argumentation-schemes-file" {:value ""
                                       :name "Argumentation Schemes File"
                                       :type :file}
         "rules-directory" {:value ""
                            :name "Rules Directory"
                            :type :directory}}))

(defn load-properties []
  (letfn [(remove-meta-data
           [fileproperties]
           (reduce (fn [fileproperties [k v]]
                     (if (.contains k ".")
                       (dissoc fileproperties k)
                       fileproperties)) fileproperties fileproperties))
          
          (str-type
           [s]
           ({"file" :file "directory" :directory "string" :string} s))
          
          (extract-meta-data
           [fileproperties property]
           (prn fileproperties)
           (let [[k v] property]
             {k {:value v
                 :name (fileproperties (str k ".name"))
                 :type (str-type (fileproperties (str k ".type")))}}))]
    (let [fileproperties (try (carneades.config.reader/read-properties
                               *properties-path*)
                              (catch java.io.FileNotFoundException e (prn e) nil))
          properties (remove-meta-data fileproperties)
          properties (apply merge
                            (map #(extract-meta-data fileproperties %) properties))]
      (merge *default-values* properties))))

(defn store-properties [properties]
  (letfn [(type-str
           [type]
           ({:file "file" :directory "directory" :string "string"} type))]
   (let [props (reduce (fn [props [k v]]
                         (let [{:keys [value name type]} v]
                           (assoc props
                             k value
                             (str k ".name") name
                             (str k ".type") (type-str type))))
                       {} properties)]
     (prn "props =")
     (prn props)
     (carneades.config.reader/save-properties props *properties-path*))))
