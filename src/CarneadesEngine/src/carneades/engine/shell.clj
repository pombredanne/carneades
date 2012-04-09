;;; Copyright (c) 2010-2011 Fraunhofer Gesellschaft 
;;; Licensed under the EUPL V.1.1

(ns ^{:doc "Utilities for interacting with the Carneades engine from a command line."}
  carneades.engine.shell
  (:use carneades.engine.statement
        carneades.engine.unify
        carneades.engine.argument-graph
        carneades.engine.argument-construction
        carneades.engine.argument-evaluation
        carneades.engine.caes
        carneades.engine.ask))

(defn make-engine
  "argument-graph integer (seq-of literal) (seq-of generator) -> 
   literal -> argument-graph)"
  ([max-goals facts generators]
    (make-engine (make-argument-graph) max-goals facts generators))
  ([argument-graph max-goals facts generators]
     (fn [issue]
       (construct-arguments argument-graph issue max-goals facts generators))))
  
(defn argue
  "engine argument-evaluator literal  -> argument-graph
   The evaluator is optional. If none is provided the arguments 
   are constructed but not evaluated."
  ([engine evaluator issue]
    {:pre [(literal? issue)]}
    (evaluate evaluator (engine issue)))
  ([engine issue]
    (engine issue)))  

(defn ask 
  "engine evaluator literal -> (seq-of literal)" 
  ([engine query]
    (ask engine carneades-evaluator query))
  ([engine evaluator query] 
   {:pre [(literal? query)]}
    (mapcat (fn [sn] 
              (let [subs (unify (statement-node-atom sn) (literal-atom query))]
                (if (not subs) 
                    ()
                    (if (or (and (literal-pos? query) (in-node? sn)) 
                            (and (literal-neg? query) (out-node? sn)))
                        (list (apply-substitutions subs query))))))
            (vals (:statement-nodes (argue engine evaluator query))))))

(defn in? 
  "argument-graph literal -> boolean"
  [ag query]
  (let [sn (get-statement-node ag (literal-atom query))]
    (if (nil? sn) 
      false
      (if (literal-pos? query)
        (in-node? sn)
        (out-node? sn)))))

(defn in-statements
  "argument-graph -> set of statement ids"
  [ag]
  (set (map :id (filter (fn [sn] 
                       (and (:value sn)
                            (= 1.0 (:value sn)))) 
                     (vals (:statement-nodes ag))))))

(defn out? 
  "argument-graph literal -> boolean"
  [ag query]
  (let [sn (get-statement-node ag (literal-atom query))]
    (if (nil? sn)
      false
      (if (literal-pos? query)
        (out-node? sn)
        (in-node? sn)))))

(defn out-statements
  "argument-graph -> set of statement ids"
  [ag]
  (set (map :id (filter (fn [sn]
                       (and (:value sn)
                            (= 0.0 (:value sn))))
                     (vals (:statement-nodes ag))))))

(defn undecided? 
  "argument-graph literal -> boolean"
  [ag query]
  (let [sn (get-statement-node ag (literal-atom query))]
    (if (nil? sn)
      false
      (undecided-node? sn))))

(defn undecided-statements
  "argument-graph -> set of statement ids"
  [ag]
  (set (map :id (filter (fn [sn]
                       (not (contains? #{1.0 0.0} (:value sn)))) 
                     (vals (:statement-nodes ag))))))


