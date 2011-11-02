(ns ^{:doc "Defines the protocol for argument generators."}
      carneades.engine.argument-generator
   (:use carneades.engine.argument
         carneades.engine.statement
         carneades.engine.unify))

; The record to be returned by argument generators.
(defrecord Response
  [substitutions   ; (term -> term) map
   assumptions     ; sequence of statements
   argument])      ; argument | nil

(defn make-response [subs asms arg] (Response. subs asms arg))

;  Protocol for argument evaluation structures.
(defprotocol ArgumentGenerator
  (generate [this stmt subs])) ; "argument-generator statement substitutions -> (seq-of response)"
