;;; Copyright © 2010 Fraunhofer Gesellschaft 
;;; Licensed under the EUPL V.1.1

(ns carneades.mapcomponent.map
  (:use clojure.contrib.def
        carneades.mapcomponent.map-styles
        carneades.engine.argument
        carneades.engine.statement)
  (:import javax.swing.SwingConstants
           (com.mxgraph.util mxConstants mxUtils mxCellRenderer mxPoint mxEvent
                             mxEventSource$mxIEventListener mxUndoManager)
           com.mxgraph.swing.util.mxGraphTransferable
           com.mxgraph.swing.handler.mxRubberband
           (com.mxgraph.view mxGraph mxStylesheet)
           (com.mxgraph.model mxCell mxGeometry)
           com.mxgraph.layout.hierarchical.mxHierarchicalLayout
           com.mxgraph.layout.mxStackLayout
           com.mxgraph.swing.mxGraphComponent
           com.mxgraph.swing.mxGraphOutline
           java.awt.event.MouseWheelListener
           java.awt.print.PrinterJob
           java.awt.Toolkit
           java.io.ByteArrayOutputStream
           javax.imageio.ImageIO
           javax.swing.ImageIcon
           (java.awt.datatransfer Transferable DataFlavor)
           (java.awt Color BasicStroke)))

(defn- stmt-to-str [ag stmt stmt-str]
  (let [formatted (stmt-str stmt)]
    (cond (and (in? ag stmt) (in? ag (statement-complement stmt)))
          (str "✔✘ " formatted)
           
          (in? ag stmt)
          (str "✔ " formatted)
           
          (in? ag (statement-complement stmt))
          (str "✘ " formatted)

          (questioned? ag stmt)
          (str "? " formatted)
           
          :else formatted)))

(defrecord StatementCell [ag stmt stmt-str formatted] Object
  (toString
   [this]
   formatted))

(defrecord ArgumentCell [arg] Object
  (toString
   [this]
   (if (= (:direction arg) :pro) "+" "‒")))

(defrecord PremiseCell [arg pm] Object
  (toString
   [this]
   (str pm)))

(defn- configure-graph [^mxGraph g]
  (let [stroke (BasicStroke. 5 BasicStroke/CAP_BUTT,
                             BasicStroke/JOIN_MITER
                             10.0
                             (into-array Float/TYPE [3 3])
                             0.0)
        color Color/orange]
    (set! mxConstants/VERTEX_SELECTION_COLOR color)
    (set! mxConstants/EDGE_SELECTION_COLOR color)
    (set! mxConstants/VERTEX_SELECTION_STROKE stroke)
    (set! mxConstants/EDGE_SELECTION_STROKE stroke))
  (doto g
    ;; (.setAllowNegativeCoordinates false)
    ;; seems there is a bug with stacklayout and setCellsLocked
    ;; so setCellsLocked is called after the layout
    ;; (.setCellsLocked true)
    (.setEdgeLabelsMovable false)
    (.setVertexLabelsMovable false)
    (.setCellsDisconnectable false)
    (.setCellsBendable false)
    ))

(defvar- *mincellwidth* 70)
(defvar- *mincellheight* 40)

(defn- getx [^mxCell vertex]
  (.. vertex getGeometry getX))

(defn- setx [^mxCell vertex x]
  (.. vertex getGeometry (setX x)))

(defn- gety [^mxCell vertex]
  (.. vertex getGeometry getY))

(defn- sety [^mxCell vertex y]
  (.. vertex getGeometry (setY y)))

(defn adjust-size [v]
  "the the vertex to minimum size"
  (let [geo (.getGeometry v)
        w (.getWidth geo)
        h (.getHeight geo)]
    (when (< h *mincellheight*)
      (.setHeight geo *mincellheight*))
    (when (< w *mincellwidth*)
      (.setWidth geo *mincellwidth*))))

(defn- insert-vertex [^mxGraph g parent name style]
  (let [v (.insertVertex g parent nil name 10 10 40 40 style)]
    (.updateCellSize g v)
    (adjust-size v)
    v))

(defn- insert-edge [^mxGraph g parent userobject begin end style]
  (.insertEdge g parent nil userobject begin end style))

(defvar- *ymargin* 10)
(defvar- *xmargin* 10)

(defn- translate-right [^mxGraph g p cells]
  (let [model (.getModel g)
        defaultparent (.getDefaultParent g)
        minx (reduce (fn [acc vertex]
                       (min (getx vertex) acc))
                     0
                     cells)
        translation (+ *xmargin* (- minx))]
    (doseq [cell cells]
      (setx cell (+ (getx cell) translation))
      (sety cell (+ (gety cell) *ymargin*)))
    (doseq [edge (.getChildCells g defaultparent false true)]
      (let [controlpoints (or (.. edge getGeometry getPoints) ())]
        (doseq [point controlpoints]
          (let [x (.getX point)
                y (.getY point)]
            (.setX point (+ x translation))
            (.setY point (+ y *ymargin*))))))
    ;; (.. g getView (scaleAndTranslate 1 translation *ymargin*))
))

(defn- print-debug [g]
  (let [defaultparent (.getDefaultParent g)
        edges (.getChildCells g defaultparent false true)]
    (doseq [edge edges]
      (let [x (getx edge)
            y (gety edge)
            controlpoints (or (.. edge getGeometry getPoints) ())]
        (printf "edge %s [%s %s] \n" edge x y)
        (printf "control points = {")
        (doseq [point controlpoints]
          (printf "[%s %s], " (.getX point) (.getY point)))
        (printf "}\n")))))

(defn- hierarchicallayout [^mxGraph g p vertices]
  (let [layout (mxHierarchicalLayout. g SwingConstants/EAST)]
    (.setAllowNegativeCoordinates g false)
    (doto layout
      (.setFineTuning true)
      (.execute p)
      )
    ;; negative coordinates are used by the layout algorithm
    ;; even with setAllowNegativeCoordinates set to false.
    ;; we translate to make all edges and vertices visible
    (translate-right g p vertices)))

(defvar- *orphan-offset* 50)

(defn- align-orphan-cells [^mxGraph g p cells]
  "align orphan cells on the right of the graph, with a stacklayout"
  (letfn [(isorphan?
           [vertex]
           (empty? (.getEdges g vertex)))]
    (let [[orphans maxx-notorphan]
          (reduce (fn [acc vertex]
                    (let [[orphans maxx-notorphan] acc
                          width (.. vertex getGeometry getWidth)
                          x (+ width (getx vertex))]
                      (cond (isorphan? vertex)
                            [(conj orphans vertex) maxx-notorphan]
                            
                            (> x maxx-notorphan)
                            [orphans x]
                            
                            :else acc)))
                  ['() 0]
                  cells)
          yorigin 20
          stackspacing 20]
      (loop [orphans orphans
             y yorigin]
        (when-not (empty? orphans)
          (let [orphan (first orphans)
                height (.. orphan getGeometry getHeight)]
            (setx orphan (+ maxx-notorphan *orphan-offset*))
            (sety orphan y)
            (recur (rest orphans) (+ y height stackspacing))))))))

(defn- do-layout [g p cells]
  (hierarchicallayout g p cells)
  (align-orphan-cells g p cells))

(defn- layout [g p vertices]
  (do-layout g p (vals vertices)))

(defn- add-statement [g p ag stmt vertices stmt-str]
  (assoc vertices
    stmt
    (insert-vertex g p (StatementCell. ag stmt stmt-str (stmt-to-str ag stmt stmt-str))
                   (get-statement-style ag stmt))))

(defn- add-statements [g p ag stmt-str]
  "add statements and returns a map statement -> vertex"
  (reduce (fn [vertices statement]
            (add-statement g p ag statement vertices stmt-str))
          {} (map node-statement (get-nodes ag))))

(defn- add-conclusion-edge [g p arg statement vertices]
  (insert-edge g p (ArgumentCell. arg) (vertices (argument-id arg))
               (vertices statement)
               (get-conclusion-edge-style arg)))

(defn- add-argument-edge [g p arg premise argid vertices]
  (insert-edge g p (PremiseCell. arg premise) (vertices (premise-atom premise))
               (vertices argid) (get-edge-style premise)))

(defn- add-argument-edges [g p ag arg vertices]
  (add-conclusion-edge g p arg (argument-conclusion arg) vertices)
  (dorun
   (map #(add-argument-edge g p arg % (argument-id arg) vertices)
        (argument-premises arg))))

(defn- add-edges [g p ag vertices]
  (dorun
   (map #(add-argument-edges g p ag % vertices) (arguments ag)))
  vertices)

(defvar- *argument-width* 32)
(defvar- *argument-height* 32)

(defn- add-argument [g p ag arg vertices]
  (let [vertex
        (insert-vertex g p (ArgumentCell. arg)
                       (get-argument-style ag arg))]
    (.. vertex getGeometry (setWidth *argument-width*))
    (.. vertex getGeometry (setHeight *argument-height*))
    (assoc vertices (argument-id arg) vertex)))

(defn- add-arguments [g p ag vertices]
  (reduce (fn [vertices arg]
            (add-argument g p ag arg vertices))
          vertices (arguments ag)))

(defn- create-graph [ag stmt-str]
  (let [g (mxGraph.)
        p (.getDefaultParent g)]
    (try
     (register-styles (.getStylesheet g))
     (configure-graph g)
     (.. g getModel beginUpdate)
     (->> (add-statements g p ag stmt-str)
          (add-arguments g p ag)
          (add-edges g p ag)
          (layout g p))
     (.setCellsLocked g true)
     (finally
      (.. g getModel endUpdate)))
    g))

(defn export-graph [graphcomponent filename]
  "Saves the graph on disk. Only SVG format is supported now.

   Throws java.io.IOException"
  (let [g (.getGraph (:component graphcomponent))]
    (mxUtils/writeFile (mxUtils/getXml
                        (.. (mxCellRenderer/createSvgDocument g nil 1 nil nil)
                            getDocumentElement))
                       filename)))

;; (defmacro with-restore-translate [g & body]
;;   "executes body and restores the initial translation of the graph after"
;;   `(do
;;      (let [point# (.. ~g getView getTranslate)
;;            x# (.getX point#)
;;            y# (.getY point#)]
;;        ~@body
;;        (let [scale# (.. ~g getView getScale)]
;;          (.. ~g getView (scaleAndTranslate scale# x# y#))))))

(defn zoom-in [graphcomponent]
  (let [g (.getGraph graphcomponent)]
    (.zoomIn graphcomponent)))

(defn zoom-out [graphcomponent]
  (let [g (.getGraph graphcomponent)]
    (when (> (.. g getView getScale) 0.1)
      (.zoomOut graphcomponent))))

(defn zoom-reset [graphcomponent]
  (let [g (.getGraph graphcomponent)]
    (.. g getView (scaleAndTranslate 1 0 0))))

(deftype MouseListener [g graphcomponent] MouseWheelListener
  (mouseWheelMoved
   [this event]
   (when (or (instance? mxGraphOutline (.getSource event))
             (.isControlDown event))
     (if (neg? (.getWheelRotation event))
       (zoom-in graphcomponent)
       (zoom-out graphcomponent)))))

(defn- add-mouse-zoom [g graphcomponent]
  (.addMouseWheelListener graphcomponent (MouseListener. g graphcomponent)))

(defn- add-undo-manager [g]
  (let [undomanager (mxUndoManager.)
        undo-handler (proxy [mxEventSource$mxIEventListener] []
                       (invoke
                        [sender event]
                        (prn "undo-handler!")
                        (.undoableEditHappened undomanager
                                               (.getProperty event "edit"))))
        undo-sync-handler (proxy [mxEventSource$mxIEventListener] []
                            (invoke
                             [sender event]
                             ;; Keeps the selection in sync with the command history
                             (let [changes (.getChanges (.getProperty event "edit"))]
                               (prn "changes = ")
                               (prn changes)
                              (.setSelectionCells
                               g (.getSelectionCellsForChanges g changes)))))]
    (.. g getModel (addListener mxEvent/UNDO undo-handler))
    (.. g getView (addListener mxEvent/UNDO undo-handler))
    ;; (.addListener undomanager mxEvent/UNDO undo-sync-handler)
    ;; (.addListener undomanager mxEvent/REDO undo-sync-handler)
    undomanager))

(defn- select-current-cell [graphcomponent]
  ;; refresh the selection to refresh the panel properties
  (let [g (.getGraph graphcomponent)
        selectionmodel (.getSelectionModel g)]
    (when-let [cell (.getCell selectionmodel)]
      (.setCell selectionmodel cell))))

(defn undo [graphcomponent]
  (prn "map-undo!")
  (.undo (:undomanager graphcomponent))
  (select-current-cell (:component graphcomponent)))

(defn redo [graphcomponent]
  (prn "map-redo!")
  (.redo (:undomanager graphcomponent))
  (select-current-cell (:component graphcomponent)))

(defn create-graph-component [ag stmt-str]
  (let [g (create-graph ag stmt-str)
        graphcomponent (proxy [mxGraphComponent] [g]
                         ;; no icon for groups
                         ;; allow invisible groups
                         (getFoldingIcon
                          [state]
                          nil))
        undomanager (add-undo-manager g)
        rubberband (mxRubberband. graphcomponent)]
    (.setConnectable graphcomponent false)
    (add-mouse-zoom g graphcomponent)
    {:component graphcomponent :undomanager undomanager}))

(defn- find-statement-cell [graph stmt]
  (loop [vertices (seq (.getChildVertices graph
                                          (.getDefaultParent graph)))]
    (when-let [cell (first vertices)]
      (let [userobject (.getValue cell)]
        (if (and (instance? StatementCell userobject)
                 (= (:stmt userobject) stmt))
          cell
          (recur (rest vertices)))))))

(defn select-statement [component stmt stmt-fmt]
  (let [component (:component component)
        graph (.getGraph component)]
    (when-let [cell (find-statement-cell graph stmt)]
      (.setSelectionCell graph cell)
      (.scrollCellToVisible component cell))))

(defn add-node-selection-listener [graphcomponent listener & args]
  "Adds a selection listener to the map. When a cell is selected, listener 
   will be invoked with the userobject of the cell as its first argument 
   followed by args.
   Userobject can be StatementCell, ArgumentCell, PremiseCell or nil"
  (let [component (:component graphcomponent)
        selectionmodel (.getSelectionModel (.getGraph component))]
    (.addListener selectionmodel mxEvent/CHANGE
                  (proxy [mxEventSource$mxIEventListener] []
                    (invoke
                     [sender event]
                     (when-let [cell (.getCell selectionmodel)]
                       (let [userobject (.getValue cell)]
                         (apply listener userobject args))))))))

(defn scale-page [swingcomponent scale]
  (if (nil? scale)
    (.setPageScale swingcomponent mxGraphComponent/DEFAULT_PAGESCALE)
    (.setPageScale swingcomponent scale)))

(defn- get-vertices [g p]
  (seq (.getChildVertices g p)))

(defn change-statement-content [graphcomponent ag oldstmt newstmt]
  (let [component (:component graphcomponent)
        graph (.getGraph component)
        cell (find-statement-cell graph oldstmt)
        stmt-str (:stmt-str (.getValue cell))
        stmt (StatementCell. ag newstmt stmt-str (stmt-to-str ag newstmt stmt-str))
        p (.getDefaultParent graph)
        model (.getModel graph)]
    (prn "change-statement-content")
    (try
      (.. model beginUpdate)
      (.setValue model cell stmt)
      (.setStyle model cell (get-statement-style ag newstmt))
      (.updateCellSize graph cell)
      (adjust-size cell)
      (do-layout graph p (get-vertices graph p))
      (finally
       (.. model endUpdate)
       (.refresh component)))))

(defn- change-cell-and-styles [component ag
                               update-statement-object
                               update-statement-style
                               update-premise-object
                               update-premise-style
                               update-argument-object
                               update-argument-style]
  (let [graph (.getGraph component)
        model (.getModel graph)]
    (let [p (.getDefaultParent graph)]
      (try
        (.. model beginUpdate)
        (doseq [cell (.getChildCells graph p true true)]
          (let [val (.getValue cell)]
            (cond (instance? StatementCell val)
                  (do
                    (.setValue model cell (update-statement-object val))
                    (.setStyle model cell (update-statement-style val (.getStyle cell))))

                  (instance? PremiseCell val)
                  (do
                    (.setValue model cell (update-premise-object val))
                    (.setStyle model cell (update-premise-style val (.getStyle cell))))
                  
                  (and (instance? ArgumentCell val) (.isVertex cell))
                  (do
                    (.setValue model cell (update-argument-object val))
                    (.setStyle model cell (update-argument-style val (.getStyle cell))))

                  ;;TODO : conclusion edge
                  )))
        (finally
         (.. model endUpdate)
         (.refresh component))))))

(defn change-all-cell-and-styles
  ([component ag]
     (letfn [(do-no-change-style
              [userobject style]
              style)]
       (change-all-cell-and-styles component ag identity do-no-change-style)))
  
  ([component ag update-pm-object update-pm-style]
     (letfn [(update-stmt-object
              [userobject]
              (let [stmt-str (:stmt-str userobject)
                    stmt (:stmt userobject)]
                (StatementCell. ag stmt stmt-str (stmt-to-str ag stmt stmt-str))))

             (update-stmt-style
              [userobject oldstyle]
              (get-statement-style ag (:stmt userobject)))
          
             (update-argument-style
              [userobject oldstyle]
              (get-argument-style ag (:arg userobject)))
             
             ]
       (change-cell-and-styles component ag
                               update-stmt-object
                               update-stmt-style
                               update-pm-object
                               update-pm-style
                               identity
                               update-argument-style))))

(defn change-statement-status [graphcomponent ag stmt]
  (let [component (:component graphcomponent)]
    (change-all-cell-and-styles component ag)))

(defn change-statement-proofstandard [graphcomponent ag stmt]
  (let [component (:component graphcomponent)]
    (change-all-cell-and-styles component ag)))

(deftype ImageSelection [data]
  Transferable
  (getTransferDataFlavors
   [this]
   (into-array DataFlavor [DataFlavor/imageFlavor]))

  (isDataFlavorSupported
   [this flavor]
   (= DataFlavor/imageFlavor flavor))

  (getTransferData
   [this flavor]
   (when (= DataFlavor/imageFlavor flavor)
     (.getImage (ImageIcon. data)))))

(defn copyselection-toclipboard [graphcomponent]
  (let [component (:component graphcomponent)
        graph (.getGraph component)
        selectionmodel (.getSelectionModel graph)
        selectedcells (.getCells selectionmodel)
        bufferedimg (mxCellRenderer/createBufferedImage
             graph selectedcells 1 nil (.isAntiAlias component) nil (.getCanvas component))
        os (ByteArrayOutputStream.)
        res (ImageIO/write bufferedimg "png" os)
        imgselection (ImageSelection. (.toByteArray os))
        clipboard (.getSystemClipboard (.getToolkit component))]
    (.setContents clipboard imgselection nil)))

(defn select-all [graphcomponent]
  (let [component (:component graphcomponent)
        graph (.getGraph component)
        cells (.getChildCells graph (.getDefaultParent graph) true true)
        selectionmodel (.getSelectionModel graph)]
    (.setCells selectionmodel cells)))

(defn change-title [graphcomponent ag title]
  (let [component (:component graphcomponent)]
    (change-all-cell-and-styles component ag)))

(defn change-premise-polarity [graphcomponent ag oldarg arg pm]
  (let [component (:component graphcomponent)]
    (letfn [(update-premise-object
             [userobject]
             (let [cellargid (:id (:arg userobject))
                   oldpm (:pm userobject)]
               (if (and (= cellargid (:id oldarg))
                        (= (:atom pm) (:atom oldpm)))
                 (PremiseCell. arg pm)
                 userobject)))
            
            (update-premise-style
             [userobject oldstyle]
             (let [oldpm (:pm userobject)
                   cellarg (:arg userobject)
                   style (get-edge-style oldpm)
                   cellargid (:id (:arg userobject))]
               (if (and (= cellargid (:id oldarg))
                        (= (:atom pm) (:atom oldpm)))
                 (get-edge-style pm)
                 style)))]
      (change-all-cell-and-styles component ag update-premise-object update-premise-style))))