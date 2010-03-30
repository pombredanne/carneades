;;; Carneades Argumentation Library and Tools.
;;; Copyright (C) 2010 Thomas F. Gordon, Fraunhofer FOKUS, Berlin
;;; 
;;; This program is free software: you can redistribute it and/or modify
;;; it under the terms of the GNU Lesser General Public License version 3 (LGPL-3)
;;; as published by the Free Software Foundation.
;;; 
;;; This program is distributed in the hope that it will be useful, but
;;; WITHOUT ANY WARRANTY; without even the implied warranty of
;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
;;; General Public License for details.
;;; 
;;; You should have received a copy of the GNU Lesser General Public License
;;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns carneades.engine.argument
  (:use clojure.test
        clojure.set
        clojure.contrib.def
        clojure.contrib.pprint
        carneades.engine.utils
        carneades.engine.statement
        carneades.engine.proofstandard))

(set! *assert* true)

(declare update-statement assert-arguments) ; forward declaration

(defstruct- premise-struct
  :atom ; an atomic statement
  :polarity      ; boolean true => positive premise | false => negative premise
  :role ; string, the role of the premise in the argumentation schemed applied
        ; nil if none
  :type ; ::ordinary-premise | ::assumption | ::exception
  )

(defvar- ordinary-premise-struct
  (apply create-struct (keys (struct premise-struct))))

(defvar- assumption-struct
  (apply create-struct (keys (struct premise-struct))))

(defvar- exception-struct
  (apply create-struct (keys (struct premise-struct))))

(derive ::ordinary-premise ::premise)
(derive ::assumption ::premise)
(derive ::exception ::premise)

;; constructors for premises
(defn ordinary-premise [& vals]
     (apply struct ordinary-premise-struct (concat vals [::ordinary-premise])))

(defn assumption [& vals]
     (apply struct assumption-struct (concat vals [::assumption])))

(defn exception [& vals]
  (apply struct exception-struct (concat vals [::exception])))

;; abbreviations for constructing premises with empty roles

(defn pm [s]
  "statement -> ordinary-premise"
  (ordinary-premise (statement-atom s) (statement-pos? s) nil))

(defn am [s]
  "statement -> assumption"
  (assumption (statement-atom s) (statement-pos? s) nil))

(defn ex [s]
  "statement -> exception"
  (exception (statement-atom s) (statement-pos? s) nil))

(defn premise-pos? [p]
  (:polarity p))

(defn premise-neg? [p]
  (not (:polarity p)))

(defn ordinary-premise? [p]
  (isa? (:type p) ::ordinary-premise))

(defn assumption? [p]
  (isa? (:type p) ::assumption))

(defn exception? [p]
  (isa? (:type p) ::exception))

(defn premise-statement [p]
  (if (premise-pos? p)
    (:atom p)
    (statement-complement (:atom p))))

(defn assumption? [p]
  (isa? (:type p) ::assumption))

(defn exception? [p]
  (isa? (:type p) ::assumption))

(defn premise-atom [p]
  (:atom p))

(defn premise= [p1 p2]
  (and (or (= (:type p1) (:type p2))
           (and (premise-neg? p1)
                (premise-neg? p2)))
       (statement= (:atom p1) (:atom p2))))

(defstruct- argument-struct
  :id ; symbol
  :applicable ; boolean
  :weight ; 0.0..1.0
  :direction ; :pro | :con
  :conclusion ; statement
  :premises ; seq-of premise
  :scheme ; a string describing or naming the scheme applied | nil
)

(def *default-weight* 0.5)

(defn argument
  ([id direction conclusion premises]
     (struct argument-struct id false *default-weight* direction conclusion
             premises nil))
  ([id direction conclusion premises scheme]
     (struct argument-struct id false *default-weight* direction conclusion
             premises scheme))
  ([id applicable weight direction conclusion premises scheme]
     (struct argument-struct id applicable weight direction
             conclusion premises scheme)))

(defn argument-id [a]
  (:id a))

(defn argument-scheme [a]
  (:scheme a))

(defn argument-direction [a]
  {:post [(not (nil? %))]}
  (:direction a))

(defn argument-conclusion [a]
  (:conclusion a))

(defn argument-premises [a]
  (:premises a))

;; Implicit premises, i.e. enthymemes, may be revealed using the  add-premise
;; function.

(defn pro [id conclusion premises]
  (argument id false *default-weight* :pro conclusion premises nil))

(defn con [id conclusion premises]
  (argument id false *default-weight* :con conclusion premises nil))

(defn assoc-applicability [arg applicability]
  "argument boolean -> argument"
  (assoc arg :applicable applicability))

(defmacro defargument [id definition]
  "Define an argument with identifiant id and 
   assign it to the variable named id with the def method

   Example: (defargument b1 (pro not-property 
                              (pm possession-required)
                              (pm no-possession)
                              (pm foxes-are-wild)))"
  `(def ~id (make-arg ~id ~definition)))

(defmacro make-arg
  "Like defargument but does not assign the created argument to anything,
   just returns it."
  ([id definition]
     (letfn [(prefixns
           [x sq]
           (let [f (first sq)
                 r (rest sq)]
             (cons (symbol (str x "/" f)) r)))]
    (let [cns (namespace ::here)
          dir (first definition)
          nsdir (symbol (str cns "/" dir))
          conclusion (second definition)
          premises (map #(prefixns cns %) (drop 2 definition))]
      `(~nsdir (quote ~id) ~conclusion (list ~@premises)))))
  ([definition]
     `(arg ~(gensym "a") ~definition)))

;; use (prn-str object) and (read-string s) to read/write argument
;; structures

(defn argument-variables [arg]
  "argument -> (seq-of symbol)

   Returns a sequence containing the variables of the argument arg"
  (distinct (concat (mapcat #(variables (:atom %)) (:premises arg))
                    (variables (:conclusion arg)))))

(defn instantiate-argument [arg subs]
  "argument substitutions -> argument

   Instanciate the variable of an argument by applying substitions"
  (assoc arg
    :premises (map #(assoc :atom (subs (:atom %))) (:premises arg))
    :conclusion (subs (:conclusion arg))))

(defn add-premise [arg p]
  (assoc arg :applicable false :premises (cons p (:premises arg))))

;; se        ; scintilla of the evidence
;;                  ba        ; best argument
;; (was "preponderance of the evidence")
;;                  dv        ; dialectical validity
;;                  pe        ; preponderance of the evidence
;;                  cce       ; clear and convincing evidence
;;                  brd       ; beyond a reasonable doubt
(defvar- *default-proof-standard* :dv)

(defstruct- node-struct
  :statement ; statement
  :status ; status
  :standard ; proof standard
  :acceptable ; boolean
  :complement-acceptable ; boolean
  :premise-of ; (set-of symbol) where each symbol is an argument id
  :conclusion-of ; (set-of symbol) where each symbol is an argument id
  )

(defn node [s]
  "Builds a new node from a statement with no pro- or con-arguments"
  (struct node-struct
          s
          :stated
          *default-proof-standard*
          false
          false
          #{}
          #{}))

(defn nodes [s]
  ;; this function is not used!
  "(seq-of statement) -> node table

   Builds a node-table from a list of statements "
  ; take care here, = is used by the map, not statement=, to
  ; order element
  (reduce (fn [nodes x]
            (let [sym (statement-symbol x)]
              (assoc-in nodes [sym x] (node x))))
          {} s))

(defn node-statement [n]
  (:statement n))

(defstruct- argument-graph-struct
  :id ; symbol
  :title ; string
  :main-issue ; statement | nil
  :nodes ; map: symbol -> statement -> node
  :arguments ; map: argument-id -> argument
  )

(defn argument-graph
  ([]
     (struct argument-graph-struct (gensym "ag") "" nil {} {}))
  ([exprs]
     "converts a seq of arguments into an argument 
      graph"
     (assert-arguments (argument-graph) exprs))
  ([id title main-issue]
     (struct argument-graph-struct id title main-issue {} {}))
  ([id title main-issue nodes]
     (struct argument-graph-struct id title main-issue nodes {}))
  ([id title main-issue nodes arguments]
     (struct argument-graph-struct id title main-issue nodes arguments)))

(defvar *empty-argument-graph* (argument-graph))

(defn get-node [ag s]
  "argument-graph statement -> node | nil"
  (if-let [n (get-in ag
                  [:nodes (statement-symbol (statement-atom s))
                   (statement-atom s)])]
    n
    (node s)))

(defn status [ag s]
  "argument-graph statement -> status"
  (let [n (get-node ag s)
        st (:status n)]
    (if (statement-pos? s)
      st
      (condp = st
        :stated :stated
        :questioned :questioned
        :accepted :rejected
        :rejected :accepted
        :unstated))))

(defn proof-standard [ag s]
  (:standard (get-node ag s)))

(defn- prior [a1 a2]
  ; this function is not used!
  (> (:weight a1)
     (:weight a2)))

(defn- add-node [ag n]
  {:pre [(not (nil? ag))]}
  "argument-graph node -> argument-graph 

   Add a node to the nodes table of an argument graph and replace 
   the nodes table of the argument graph with this new table "
  (assoc-in ag [:nodes (statement-symbol (:statement n)) (:statement n)] n))

(defn- node-in? [n positive]
  "argument-graph node boolean -> boolean"
  (if positive
    (or (= (:status n) :accepted)
        (:acceptable n))
    (or (= (:status n) :rejected)
        (:complement-acceptable n))))

(defn- in? [ag s]
  "looks up the cached 'in' status of the statement in the agreement graph
   argument-graph statement -> boolean "
  (let [n (get-node ag s)]
    (node-in? n (statement-pos? s))))

(defn- out? [ag s]
  (not (in? ag s)))

(defn- holds? [ag p]
  "argument-graph premise -> boolean"
  (let [n (get-node ag (:atom p))]
    (cond (ordinary-premise? p)
          (condp = (:status n)
            :accepted (premise-pos? p)
            :rejected (premise-neg? p)
            :questioned (in? ag (premise-statement p))
            :stated (in? ag (premise-statement p))
            :unstated false
            false)
          (assumption? p)
          (condp = (:status n)
            :stated true  ; wether the premise is positive or negative
            :unstated false
            :accepted (premise-pos? p)
            :rejected (premise-neg? p)
            :questioned (in? ag (premise-statement p))
            false)
          (exception? p)
          (condp = (:status n)
            :accepted (premise-neg? p)
            :rejected (premise-pos? p)
            :stated (out? ag (premise-statement p))
            :questioned (out? ag (premise-statement p))
            :unstated false
            false)
          :else (throw (Exception. (format "not a premise %s" p))))))

(defn- all-premises-hold? [ag arg]
  "argument-graph argument -> boolean"
  (every? #(holds? ag %) (:premises arg)))

(defn state [ag statements]
  "argument-graph (seq-of statement) -> argument-graph

   Changes, non-destructively, the status of each statement in the list to 
   stated in the argument graph.  Statements in the list which do not
   have a node in the argument graph are ignored. "
  (reduce #(update-statement %1 %2 :stated) ag statements))

(defn question [ag statements]
  "argument-graph (seq-of statement) -> argument-graph"
  (reduce #(update-statement %1 %2 :questioned) ag statements))

(defn accept [ag statements]
  "argument-graph (seq-of statement) -> argument-graph"
  (reduce #(update-statement %1 %2 (if (statement-pos? %2)
                                     :accepted
                                     :rejected)) ag statements))

(defn reject [ag statements]
  (reduce #(update-statement %1 %2 (if (statement-pos? %2)
                                     :rejected
                                     :accepted)) ag statements))

(defn assoc-standard [ag ps statements]
  "argument-graph  proof-standard (list-of statement) -> argument-graph"
  (reduce (fn [ag s]
            (let [n (get-node ag s)]
              (update-statement (add-node ag (assoc n :standard ps))
                                s
                                (:status n))))
          ag statements))

(defn get-argument [ag id]
  "argument-graph symbol -> argument | nil"
  ((:arguments ag) id))

(defn ids-arguments [ag ids]
  "argument-graph (seq-of symbol) -> (seq-of argument)"
  (filter identity (map #(get-argument ag %) ids)))

(defn arguments 
   " Returns all arguments pro and con of some statement in an argument graph,
     or all arguments in the argument graph, if no statement is provided
     argument-graph [statement] -> (seq-of argument)"
   ([ag s]
      (if-let [n (get-node ag s)]
        (ids-arguments ag (seq (:conclusion-of n)))
        '()))
   ([ag]
      (if-let [args (vals (:arguments ag))]
        args
        '())))

(defn pro-arguments [ag s]
  "argument-graph statement  -> (seq-of argument)"
  (let [args (arguments ag s)]
    (if (statement-pos? s)
      (filter #(= (:direction %) :pro) args)
      (filter #(= (:direction %) :con) args))))

(defn con-arguments [ag s]
  "argument-graph statement -> (seq-of argument)"
  (pro-arguments ag (statement-complement s)))

(defn schemes-applied [ag s]
  "argument-graph statement -> (seq-of symbol)"
  (let [n (get-node ag s)]
    (map :scheme )))

(defn accepted? [ag s]
  "argument-graph statement -> boolean"
  (let [n (get-node ag s)]
    (if (statement-pos? s)
      (= (:status n) :accepted)
      (= (:status n) :rejected))))

(defn rejected? [ag s]
  "argument-graph statement -> boolean"
  (let [n (get-node ag s)]
    (if (statement-pos? s)
      (= (:status n) :rejected)
      (= (:status n) :accepted))))

(defn decided? [ag s]
  "argument-graph statement -> boolean"
  (or (accepted? ag s) (rejected? ag s)))

(defn questioned? [ag s]
  "argument-graph statement -> boolean"
  (let [n (get-node ag s)]
    (= (:status n) :questioned)))

(defn stated? [ag s]
  (let [n (get-node ag s)]
    (= (:status n) :stated)))

(defn issue? [ag s]
   "argument-graph statement -> boolean
   
   An statement is an issue if it is undecided in the argument graph
   An acceptable statement is still an issue, due to nonmonotonicity:
   Additional arguments may make the statement unacceptable again."
   (not (decided? ag s)))

(defn all-premises [ag s]
 "all-premises: argument-graph statement -> (list-of premise)

  Returns the set of all the premises of all arguments pro or con the 
  statement s in the argument graph ag. The set of premises is represented
  as a list."
 (reduce #(union-if premise= (:premises %2) %1)  '() (arguments ag s)))

(defn check-acceptability [ag s]
  "argument-graph statement -> boolean"
  (let [ps (proof-standard ag s)]
    (satisfies? ag ps (pro-arguments ag s) (con-arguments ag s)
                all-premises-hold?)))

(defn acceptable? [ag s]
  "argument-graph statement -> boolean"
  (let [n (get-node ag s)]
    (if (statement-pos? s)
      (:acceptable n)
      (:complement-acceptable n))))

(defn applicable? [ag arg]
  {:post [(not (nil? %))]}
  "argument-graph argument -> boolean

   Assumes that the applicability of the argument has been 
   previously computed or updated"
  (:applicable (get-argument ag (:id arg))))

(defn depends-on? [ag s1 s2]
  "argument-graph statement statement -> boolean

   s1 depends on s2 in ag if s1 equals s2 or, recursively, some premise 
   of some argument pro or con s1 depends on s2 in ag."
  (or (statement= s1 s2)
      (some #(depends-on? ag (:atom %) s2) (all-premises ag s1))))

(defn cycle-free? [ag arg]
  "argument-graph argument -> boolean

  (cycle-free arg ag) checks whether an argument arg will not introduce a cycle
  into the argument graph ag.  An argument will not introduce a cycle if
  none of its premises depend on its conclusion in ag."
  (not-any? #(depends-on? ag (:atom %) (:conclusion arg)) (:premises arg)))

(defn relevant? [ag s g]
  "argument-graph statement statement -> boolean

  A sentence s is relevant for proving a 
  goal sentence g in ag if g depends on s in ag"
  (depends-on? ag g s))

(defn get-nodes
  "argument-graph [symbol] -> (seq-of node)
  
  returns the list of all statement nodes in the argument graph, or all
  statement nodes with the given predicate, if one is provided."
  ([ag]
     (mapcat #(vals %) (vals (:nodes ag))))
  ([ag predicate]
     (throw (Exception. "NYI"))))
     ;(vals (get-in ag [:nodes predicate]))))

(defn in-node-statements [ag n]
  "argument-graph node -> (seq-of statement)

   For a node whose statement is P, returns a list of the members of
   '(P (not P)) which are in."
  (filter identity (list (if (node-in? ag n true)
                           (:statement n))
                         (if (node-in? ag n false)
                           (statement-complement (statement-atom n))))))

(defn in-statements
 "argument-graph [symbol] -> (list-of statement)
 
  returns the list of all the in statements (literals) in the argument graph,
  or all in statements with the given predicate, if one is provided.
  Notice that for some atomic statement P, both P and (not P) may be members
  of the list of statements returned, since if a weak proof standard is assigned
  to P, both P and (not P) may be in."
 ([ag]
    (mapcat #(in-node-statements %) (get-nodes ag)))
 ([ag predicate]
    (mapcat #(in-node-statements %) (get-nodes ag predicate))))

(defn relevant-statements [ag g]
 "argument-graph statement -> (seq-of statement)

  Finds the statements in ag which are 
  relevant for proving g. Since only nodes in the graph are 
  considered, g itself will be a member of the resulting list
  only if it has a node in the argument graph."
 (filter #(relevant? ag % g) (map #(:statement %) (get-nodes ag))))

(defn update-argument [ag arg]
  {:pre [(not (nil? (:id arg)))]}
  "argument-graph argument -> argument-graph 

   updates the applicability of an argument in an argument graph and propogates 
   this change by updating the argument graph for the conclusion
   of the argument. "
  (let [old-applicability (:applicable arg)
        new-applicability (all-premises-hold? ag arg)
        ag2 (assoc-in ag [:arguments (:id arg)]
                      (assoc-applicability arg new-applicability))]
    (if (= old-applicability new-applicability)
      ag2
      ;; update the applicability of the new argument and propogate the change
      ;; by updating the conclusion of the argument
      (update-statement ag2 (:conclusion arg)))))

(defn assert-argument [ag arg]
  {:pre [(not (nil? ag))]}
  "argument-graph argument -> argument-graph

   Add the argument, arg, to the argument graph, ag,
   if doing so would not introduce a cycle. If some argument with
   the same id exists in the argument graph, then the existing argument is 
   replacd by the new argument. A node for the conclusion of the argument is 
   added to the node table of the argument graph if one does not yet exist. 
   It is the responsiblity of the protocol to question the conclusion of the 
   argument, if this is wanted. The \"applicable\" field of the argument is 
   changed to false before it is updated toassure the the acceptabiity of its 
   conclusion is checked if the argument is in fact applicable."
  (if (not (cycle-free? ag arg))
    ag
    (let [n (get-node ag (:conclusion arg))
          ;; update the node for the conclusion of the argument
          ;; :acceptable and :complement-acceptable are updated below
          ag1 (add-node ag (assoc n :conclusion-of (union #{(:id arg)}
                                                          (:conclusion-of n))))
          ;; update or create nodes for each of the premises of the argument
          ag2 (reduce (fn [ag p]
                        (let [n (get-node ag (:atom p))]
                          (add-node ag
                                    (assoc n :premise-of
                                           (union #{(:id arg)} 
                                                  (:premise-of n))))))
                      ag1 (:premises arg))]
      (update-argument ag2 (assoc arg :applicable false)))))

(defn assert-arguments [ag args]
  {:pre [(not (nil? ag))]}
  "argument-graph (seq-of argument) -> argument-graph

   asserts a list of arguments"
  (reduce (fn [ag arg] (assert-argument ag arg)) ag args))

(defn update-statement
  "argument-graph statement (or symbol nil) -> argument-graph

   updates the nodes for the given statement by changing the status of the 
   statment to new-status and recomputing the acceptability of the statement
   and its complement. If the \"in\" status of the statement
   of the node, or the complement of the statement, is affected, the change
   is propogated forwards by updating the arguments in which the statement 
   is used as a premise, which, recursively, updates the conclusions of these 
   arguments. "
  ([ag s]
     (update-statement ag s nil))
  ([ag s new-status]
     (let [n1 (get-node ag s)
           old-status (:status n1)
           n2 (assoc n1
                :status (or new-status old-status)
                :acceptable (check-acceptability ag (statement-atom s))
                :complement-acceptable
                (check-acceptability ag
                                     (statement-complement (statement-atom s))))
           ag2 (add-node ag n2)]
       (if (and (= (node-in? n1 true) (node-in? n2 true))
                (= (node-in? n1 false) (node-in? n2 false)))
         ;; then the "in" status of the statement hasn't changed and there's no
         ;; need to propogate further
         ag2
         ;; else propogate the update to the arguments in which the statement
         ;; is a premise
         (reduce (fn [ag id]
                   (if-let [arg (get-argument ag id)]
                     (update-argument ag arg)
                     ag))
                 ag2 (:premise-of n1))))))

(deftest test-argument-variables
  (let [p1 (pm '(age ?x ?y))
        p2 (pm '(father ?z ?y))
        cc '(mother ?k ?y)
        arg (argument 'arg1 'pro cc [p1 p2])
        vars (argument-variables arg)]
    (is (some #(= '?x %) vars))
    (is (some #(= '?y %) vars))
    (is (some #(= '?k %) vars))
    (is (some #(= '?z %) vars))))

(deftest test-premise-statement
  (let [stmt "Foxes are wild animals."]
    (is (= stmt (premise-statement (pm stmt))))
    (is (= (list 'not stmt)
           (premise-statement (ordinary-premise stmt false nil))))))

(deftest test-argument-variables
  (let [not-property
        (struct fatom
                "%s by pursing the ?animal did not acquire property in the %s"
                '(not-property ?person ?animal))
        possession-required
        "Property rights in wild animals may be acquired only by possession"
        no-possession (struct fatom "%s did not have possession of the %s"
                              '(no-possession ?person ?animal))
        are-wild (struct fatom "%s are wild" '(are-wild ?animals))
        arg (pro 'arg1 not-property (list (pm possession-required)
                                          (pm no-possession)
                                          (pm are-wild)))]
    (is (= '(?animal ?animals ?person) (sort (argument-variables arg))))))

(deftest test-add-premise
  (let [not-property
        "Post by pursing the fox did not acquire property in the fox"
        possession-required
        "Property rights in wild animals may be acquired only by possession"
        no-possession "Post did not have possession of the fox"
        foxes-are-wild "Foxes are wild animals"
        arg (pro 'arg1 not-property (list (pm possession-required)
                                          (pm no-possession))) 
        arg2 (add-premise arg (pm foxes-are-wild))]
    (is (= {:id 'arg1,
         :applicable false,
         :weight 0.5,
         :direction :pro,
         :conclusion
         "Post by pursing the fox did not acquire property in the fox",
         :premises
         '({:atom
            "Property rights in wild animals may be acquired only by possession",
            :polarity true,
            :role nil,
            :type :carneades.engine.argument/ordinary-premise}
           {:atom "Post did not have possession of the fox",
            :polarity true,
            :role nil,
            :type :carneades.engine.argument/ordinary-premise}),
         :scheme nil}
        arg))
    (is (= {:id 'arg1,
         :applicable false,
         :weight 0.5,
         :direction :pro,
         :conclusion
         "Post by pursing the fox did not acquire property in the fox",
         :premises
         '({:atom "Foxes are wild animals",
            :polarity true,
            :role nil,
            :type :carneades.engine.argument/ordinary-premise}
           {:atom
            "Property rights in wild animals may be acquired only by possession",
            :polarity true,
            :role nil,
            :type :carneades.engine.argument/ordinary-premise}
           {:atom "Post did not have possession of the fox",
            :polarity true,
            :role nil,
            :type :carneades.engine.argument/ordinary-premise}),
         :scheme nil}
        arg2))))

(deftest test-nodes
  (is (= {'father
       {'(father X Y)
        {:statement '(father X Y),
         :status :stated,
         :standard :dv,
         :acceptable false,
         :complement-acceptable false,
         :premise-of #{},
         :conclusion-of #{}}},
       'mother
       {{:form "blabla", :term '(mother X Y)}
        {:statement {:form "blabla", :term '(mother X Y)},
         :status :stated,
         :standard :dv,
         :acceptable false,
         :complement-acceptable false,
         :premise-of #{},
         :conclusion-of #{}},
        '(mother P A)
        {:statement '(mother P A),
         :status :stated,
         :standard :dv,
         :acceptable false,
         :complement-acceptable false,
         :premise-of #{},
         :conclusion-of #{}}}}
      (nodes (list '(mother P A) '(father X Y)
                   (struct fatom "blabla" '(mother X Y))) ))))

(deftest test-accept
  ;; test based on the Pierson-Post example
  (let [not-property (str "Post, by pursuing the fox,"
                          "did not acquire property in the fox.")
        possession-required (str "Property rights in wild animals may "
                                 "be acquired only by possession.")
        foxes-are-wild "Foxes are wild animals."
        no-possession (str "Post did not have possession of the fox.")
        pursuit-not-sufficient (str "Pursuit is not sufficient"
                                    "to acquire possession.")
        justinian "Justinian's Institutes"
        fleta "Fleta"
        bracton "Bracton"
        actual-possession-required "Actual corporal possession is required."
        puffendorf "Puffendorf"
        bynkershoek "Bynkershoek"
        mortally-wounded-deemed-possessed (str "Pursuit is sufficient to obtain"
                                               " possession when the animal "
                                               "is mortally wounded.")
        barbeyrac "Barbeyrac"
        grotius "Grotius"
        mortally-wounded "The fox was mortally wounded."
        land-owner-has-possession (str "The owner of land pursuing a "
                                       "livelihood with animals on his land is "
                                       "deemed to have possession of "
                                       "the animals.")
        livelihood-on-own-land "Post was pursing his livelihood on his own land"
        keeble "Keeble"
        certainty (str "A bright-line rule creates legal certainty, "
                       "preserving peace and order.")
        order "Peace and order is an important social value."
        a1 (make-arg a1 (pro not-property
                        (pm possession-required)
                        (pm no-possession)
                        (pm foxes-are-wild)))
        a2 (make-arg a2 (pro no-possession (pm pursuit-not-sufficient)))
        a3 (make-arg a3 (pro pursuit-not-sufficient (am justinian)))
        a4 (make-arg a4 (pro pursuit-not-sufficient (am fleta)))
        a5 (make-arg a5 (pro pursuit-not-sufficient (am bracton)))
        a6 (make-arg a6 (pro no-possession (pm actual-possession-required)))
        a7 (make-arg a7 (pro actual-possession-required (am puffendorf)))
        a8 (make-arg a8 (pro puffendorf (am bynkershoek)))
        a9 (make-arg a9 (con actual-possession-required 
                        (pm mortally-wounded-deemed-possessed)
                        (pm mortally-wounded)))
        a10 (make-arg a10 (pro mortally-wounded-deemed-possessed (am grotius)))
        a11 (make-arg a11 (pro mortally-wounded-deemed-possessed (am barbeyrac)))
        a12 (make-arg a12 (con actual-possession-required
                          (pm land-owner-has-possession)
                          (pm livelihood-on-own-land)))
        a13 (make-arg a13 (pro land-owner-has-possession (am keeble)))
        a14 (make-arg a14 (pro actual-possession-required 
                          (pm certainty)
                          (pm order)))
        args1 (argument-graph (list a1 a2 a3 a4 a5 a6 a7 a8 a9 a10 a11 a12 a13
                                    a14))
        facts1 (list foxes-are-wild possession-required certainty order)
        tompkins (accept args1 facts1)]
    (is (= (into #{} (vals (:nodes tompkins)))
           #{{"Bynkershoek"
              {:statement "Bynkershoek",
               :status :stated,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a8},
               :conclusion-of #{}}}
             {"Pursuit is sufficient to obtain possession when the animal is mortally wounded."
              {:statement
               "Pursuit is sufficient to obtain possession when the animal is mortally wounded.",
               :status :stated,
               :standard :dv,
               :acceptable true,
               :complement-acceptable false,
               :premise-of #{'a9},
               :conclusion-of #{'a11 'a10}}}
             {"Actual corporal possession is required."
              {:statement "Actual corporal possession is required.",
               :status :stated,
               :standard :dv,
               :acceptable true,
               :complement-acceptable false,
               :premise-of #{'a6},
               :conclusion-of #{'a7 'a12 'a14 'a9}}}
             {"Bracton"
              {:statement "Bracton",
               :status :stated,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a5},
               :conclusion-of #{}}}
             {"A bright-line rule creates legal certainty, preserving peace and order."
              {:statement
               "A bright-line rule creates legal certainty, preserving peace and order.",
               :status :accepted,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a14},
               :conclusion-of #{}}}
             {"Justinian's Institutes"
              {:statement "Justinian's Institutes",
               :status :stated,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a3},
               :conclusion-of #{}}}
             {"Foxes are wild animals."
              {:statement "Foxes are wild animals.",
               :status :accepted,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a1},
               :conclusion-of #{}}}
             {"Fleta"
              {:statement "Fleta",
               :status :stated,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a4},
               :conclusion-of #{}}}
             {"Post, by pursuing the fox,did not acquire property in the fox."
              {:statement
               "Post, by pursuing the fox,did not acquire property in the fox.",
               :status :stated,
               :standard :dv,
               :acceptable true,
               :complement-acceptable false,
               :premise-of #{},
               :conclusion-of #{'a1}}}
             {"Grotius"
              {:statement "Grotius",
               :status :stated,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a10},
               :conclusion-of #{}}}
             {"Post was pursing his livelihood on his own land"
              {:statement "Post was pursing his livelihood on his own land",
               :status :stated,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a12},
               :conclusion-of #{}}}
             {"Keeble"
              {:statement "Keeble",
               :status :stated,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a13},
               :conclusion-of #{}}}
             {"The owner of land pursuing a livelihood with animals on his land is deemed to have possession of the animals."
              {:statement
               "The owner of land pursuing a livelihood with animals on his land is deemed to have possession of the animals.",
               :status :stated,
               :standard :dv,
               :acceptable true,
               :complement-acceptable false,
               :premise-of #{'a12},
               :conclusion-of #{'a13}}}
             {"Barbeyrac"
              {:statement "Barbeyrac",
               :status :stated,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a11},
               :conclusion-of #{}}}
             {"Post did not have possession of the fox."
              {:statement "Post did not have possession of the fox.",
               :status :stated,
               :standard :dv,
               :acceptable true,
               :complement-acceptable false,
               :premise-of #{'a1},
               :conclusion-of #{'a6 'a2}}}
             {"The fox was mortally wounded."
              {:statement "The fox was mortally wounded.",
               :status :stated,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a9},
               :conclusion-of #{}}}
             {"Peace and order is an important social value."
              {:statement "Peace and order is an important social value.",
               :status :accepted,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a14},
               :conclusion-of #{}}}
             {"Puffendorf"
              {:statement "Puffendorf",
               :status :stated,
               :standard :dv,
               :acceptable true,
               :complement-acceptable false,
               :premise-of #{'a7},
               :conclusion-of #{'a8}}}
             {"Property rights in wild animals may be acquired only by possession."
              {:statement
               "Property rights in wild animals may be acquired only by possession.",
               :status :accepted,
               :standard :dv,
               :acceptable false,
               :complement-acceptable false,
               :premise-of #{'a1},
               :conclusion-of #{}}}
             {"Pursuit is not sufficientto acquire possession."
              {:statement "Pursuit is not sufficientto acquire possession.",
               :status :stated,
               :standard :dv,
               :acceptable true,
               :complement-acceptable false,
               :premise-of #{'a2},
               :conclusion-of #{'a5 'a4 'a3}}}}))))