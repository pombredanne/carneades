;;; Copyright © 2010 Fraunhofer Gesellschaft 
;;; Licensed under the EUPL V.1.1

(ns carneades.editor.view.editor-helpers
  (:use clojure.contrib.def
        (carneades.mapcomponent map map-edit)
        (carneades.editor.view search viewprotocol swinguiprotocol tabs tree context))
  (:import (javax.swing UIManager JFrame JFileChooser JOptionPane SwingUtilities)
           (carneades.editor.uicomponents EditorApplicationView)
           (carneades.mapcomponent.map StatementCell ArgumentCell PremiseCell)))

(defvar *frame* (EditorApplicationView/instance))
(defvar *mainIssueMenuItem* (.mainIssueMenuItem *frame*))

(defvar- *argumentPopupMenu* (.argumentPopupMenu *frame*))
(defvar- *premisePopupMenu* (.premisePopupMenu *frame*))
(defvar- *statementPopupMenu* (.statementPopupMenu *frame*))
(defvar- *mapPopupMenu* (.mapPopupMenu *frame*))
(defvar *addExistingPremiseMenuItem* (.addExistingPremiseMenuItem *frame*))
(defvar *newStatementMenuItem* (.newStatementMenuItem *frame*))

(defvar *add-existing-premise-data* (atom {:path nil :id nil :src nil}))

(defvar *statement-selection-listeners* (atom ()))
(defvar *argument-selection-listeners* (atom ()))
(defvar *premise-selection-listeners* (atom ()))
(defvar *add-existing-premise-listeners* (atom ()))

(defvar *main-issues* (atom {}))

(defn- check-link-premise [view path id obj]
  (let [data (deref *add-existing-premise-data*)]
    (when-let [src (:src data)]
      (when (and (= (:path data) path)
                 (= (:id data) id))
        ;; currently doing an 'add existing premise'?
        (doseq [{:keys [listener args]} (deref *add-existing-premise-listeners*)]
          (apply listener view path id (:arg src) (:stmt obj) args)))
      (swap! *add-existing-premise-data* assoc :src nil))))

(defn- node-selection-listener [view path id obj]
  (cond (instance? StatementCell obj)
        (do
          (let [stmt (:stmt obj)]
            (check-link-premise view path id obj)
            (prn "get = ")
            (prn (get (deref *main-issues*) [path id]))
            (prn "stmt =")
            (prn stmt)
            (if (= (get (deref *main-issues*) [path id]) stmt)
              (.setSelected *mainIssueMenuItem* true)
              (.setSelected *mainIssueMenuItem* false))
            (doseq [{:keys [listener args]} (deref *statement-selection-listeners*)]
              (apply listener path id stmt args))))

        (instance? ArgumentCell obj)
        (doseq [{:keys [listener args]} (deref *argument-selection-listeners*)]
          (apply listener path id (:arg obj) args))

        (instance? PremiseCell obj)
        (doseq [{:keys [listener args]} (deref *premise-selection-listeners*)]
          (apply listener path id (:arg obj) (:pm obj) args))))

(defn add-existing-premise-menuitem-listener [event view]
  (let [[path id] (current-graph view)]
    (when-let [component (get-component path id)]
      (let [obj (current-selected-object component)]
        (swap! *add-existing-premise-data* assoc :src obj :path path :id id)))))

(defn right-click-listener [path id component event obj]
  (let [pt (SwingUtilities/convertPoint
            (.getComponent event)
            (.getPoint event)
            component)
        x (.getX pt)
        y (.getY pt)]
    (cond (instance? ArgumentCell obj)
          (.show *argumentPopupMenu* component x y)

          (instance? PremiseCell obj)
          (.show *premisePopupMenu* component x y)

          (instance? StatementCell obj)
          (.show *statementPopupMenu* component x y)

          (nil? obj)
          (.show *mapPopupMenu* component x y)
          )))

(defn create-tabgraph-component [this path ag stmt-fmt]
  (try
    (set-busy this true)
    (let [component (create-graph-component ag stmt-fmt)]
      (add-node-selection-listener component #(node-selection-listener
                                               this path (:id ag) %))
      (add-right-click-listener component
                                (fn [event obj]
                                  (right-click-listener path
                                                        (:id ag)
                                                        (:component component)
                                                        event
                                                        obj)))
      (add-component component path ag (is-dirty? path (:id ag)))
      (set-current-ag-context path (:id ag)))
    (finally
     (set-busy this false))))
