#!r6rs

(library
 
 (carneades lkif2)
 
 (export get-document get-lkif
         read-theory theory? make-theory theory-id theory-imports theory-axioms theory-rules
         lkif-rule->rule)
 
 (import (rnrs)
         (carneades rule)
         (prefix (carneades argument) argument:)
         (carneades lib match)
         (prefix (carneades table) table:)
         (carneades lib xml sxml xpath-context_xlink))
 
 
 
 ; --------------------------------
 ; datatypes definitions
 
 ; lkif-import is the datastructure that is supposed to
 ; be returned by the import function
 (define-record-type lkif-import
   (fields context            ; context build by axioms
           argument-generator ; argument-generator from rules
           stages))           ; (list-of stages)

 (define-record-type stage
   (fields argument-graph     ; argument-graph
           context))          ; context for argument-graph
 
 ; source in lkif2
 (define-record-type source
   (fields element
           uri))
 
 ; theory in lkif2
 (define-record-type theory
   (fields id
           imports
           axioms
           rules))
 
 ; argument-graph in lkif2
 (define-record-type argument-graph
   (fields id
           title
           main-issue
           statements
           arguments))
 
 ; statement in lkif2
 (define-record-type statement
   (fields id
           value
           assumption
           standard
           atom))
 

 ; --------------------------------
 ; Type checkers 
 ; check for object-types as defined in the lkif2 grammar
 ; (only keyword is checked; length and argument-types are ignored)
 
 (define (element-type? element type)
   (and (pair? element)
        (eq? (car element) type)))
 
 (define (element-checker* type)
   (lambda (e) (element-type? e type)))
 
 (define-syntax element-types?
   (syntax-rules ()
     ((_ e t) (element-type? e t))
     ((_ e t1 ...) (or (element-type? e t1)
                       ...))))
 
 (define-syntax element-checker
   (syntax-rules ()
;     ((_ t) (element-checker* t))
     ((_ t1 ...) (lambda (e) (element-types? e t1 ...)))))
 
 
 ; --------------------------------
 ; Import functions 
 
 
 (define (import path)
   (let ((lkif-body (get-lkif (get-document path))))
     (let ((sources (read-sources lkif-body))
           (theory (read-theory lkif-body))
           (arg-graphs (read-argument-graphs lkif-body)))
       (let ((context (lkif-axioms->context (theory-axioms theory)))
             (argument-generator (lkif-rules->generator (theory-rules theory) '()))
             (stages (map argument-graph->stage arg-graphs)))
         (make-lkif-import context argument-generator stages)))))
 
 
 ; --------------------------------
 ; Selectors
 ; functions to extract the content from its context
 
 ; get-element: lkif-element proc any -> any
 (define (get-element element type)
   (or (find (element-checker type) element)
       '()))
 
 ; get-elements: lkif-element any -> (list-of any)
 (define (get-elements element type)
   (filter (element-checker type) element))
 
 ; get-attributes: lkif-element -> (list-of lkif-attributes)
 (define (get-attributes element)
   (get-element element '^))
 
 ; get-attribute: lkif-element any -> any
 (define (get-attribute element type)
   (get-element (get-attributes element) type))
           
 ; get-document: file-path -> sxml-document
 (define (get-document path)
   (sxml:document path '()))
 
 ; get-lkif: sxml-document -> lkif-body
 (define (get-lkif doc)
   (match doc
     (('*TOP* ('*PI* . p) ... ('lkif . b)) b)))
 
 ; get-attribute-value: any any -> any
 ; returns the value of an attribute if it is any,
 ; else returns default
 (define (get-attribute-value attribute default)
   (if (pair? attribute)
       (cadr attribute)
       default))
 
 ; get-statement: xsd:ID table -> struct:statement
 (define (get-statement sid tbl)
   (table:lookup tbl sid (lambda ()
                           (display "Error: Statement-ID not found: ")
                           (display sid)
                           (newline)
                           #f)))
 
 ; get-conclusion-statement: lkif-conclusion table -> struct:statement
 (define (get-conclusion-statement c tbl)
   (let ((sid (get-attribute-value (get-attribute c 'statement) "")))
     (get-statement sid tbl)))
 
 
 ; --------------------------------
 ; Constructors 
 
 
; source-to-record: lkif-source -> struct:source
 (define (source-to-record s)
   (let ((uri (get-attribute-value (get-attribute s 'uri) "")) 
         (element (get-attribute-value (get-attribute s 'element) "")))
     (make-source uri element)))
 
 ; theory-to-record: lkif-theory -> struct:theory
 (define (theory-to-record t)
   (let ((id (get-attribute-value (get-attribute t 'id) ""))
         (imports (get-elements (get-element t 'imports) 'import))
         (axioms (get-elements (get-element t 'axioms) 'axiom))
         (rules (get-elements (get-element t 'rules) 'rule)))
     (make-theory id imports axioms rules)))
 
 ; argument-graph-to-record: lkif-argument-graph -> struct:argument-graph
 (define (argument-graph-to-record ag)
   (let ((id (get-attribute-value (get-attribute ag 'id) ""))
         (title (get-attribute-value (get-attribute ag 'title) ""))
         (main-issue (get-attribute-value (get-attribute ag 'main-issue) ""))
         (statements (map statement-to-record (get-elements (get-element ag 'statements) 'statement)))
         (arguments (get-elements (get-element ag 'arguments) 'argument)))
     (make-argument-graph id title main-issue statements arguments)))
 
 ; lkif-argument-graph->stage: lkif-argument-graph -> struct:stage
 (define (lkif-argument-graph->stage ag)
   (call-with-values (lambda () (lkif-argument-graph->argument-graph/context ag))
                     (lambda (a c) (make-stage a c))))
 
 ; statement-to-record: lkif-statement -> struct:statement
 (define (statement-to-record  s)
   (let ((id (get-attribute-value (get-attribute s 'id) ""))
         (value (get-attribute-value (get-attribute s 'value) "unknown"))
         (assumption (get-attribute-value (get-attribute s 'assumption) "false"))
         (standard (get-attribute-value (get-attribute s 'standard) "BA"))
         (atom (get-element s 's)))
     (make-statement id value assumption standard atom))) 
  
 ; premise-to-record: lkif-premise tbl -> struct:premise
 (define (premise-to-record p tbl)
   (let ((polarity (get-attribute-value (get-attribute p 'polarity) "positive"))
         (exception (get-attribute-value (get-attribute p 'exception) "false"))
         (role (get-attribute-value (get-attribute p 'role) ""))
         (sid (get-attribute-value (get-attribute p 'statement) "")))
     (let ((s (statement->sexpr (get-statement sid tbl)))
           (p (string=? polarity "true")))   
       (argument:make-premise s p role))))
 
 ; argument-to-record: lkif-argument table -> struct:argument
 (define (argument-to-record a tbl)
   (let ((id (string->symbol (get-attribute-value (get-attribute a 'id) "")))
         (title (string->symbol (get-attribute-value (get-attribute a 'title) "")))
         (direction (string->symbol (get-attribute-value (get-attribute a 'direction) "pro")))
         (scheme (string->symbol (get-attribute-value (get-attribute a 'scheme) "")))
         (weight (string->symbol (get-attribute-value (get-attribute a 'weight) "0.5")))
         (conclusion (statement->sexpr (get-conclusion-statement (cdr (get-element a 'conclusion)) tbl)))
         (premises (map (lambda (x) (premise-to-record x tbl)) (get-elements (get-element a 'premises) 'premise))))
     (argument:make-argument id direction conclusion premises scheme)))
 
 


 
 ; --------------------------------
 ; Reader functions 

 
 ; read-sources: lkif-body -> (list-of struct:source)
 (define (read-sources doc-lkif)
   (map source-to-record (get-elements (get-element doc-lkif 'sources) 'source)))
 
 ; read-theory: lkif-body -> struct:theory
 (define (read-theory doc-lkif)
   (theory-to-record (get-element doc-lkif 'theory)))
 
 ; read-argument-graphs: lkif-body -> (list-of struct:argument-graphs) 
 (define (read-argument-graphs doc-lkif)
   (map argument-graph-to-record (get-elements (get-element doc-lkif 'argument-graphs) 'argument-graph)))
 
 
 
 ; --------------------------------
 ; Conversion functions 
 
 
 ; wffs- and term-conversion
 
 (define (lkif-wff->sexpr w)
   (cond ((element-type? w 's) (lkif-atom->sexpr w))
         ((element-type? w 'or) (lkif-or->sexpr w))
         ((element-type? w 'and) (lkif-and->sexpr w))
         ((element-type? w 'not) (lkif-not->sexpr w))
         ((element-type? w 'if) (lkif-if->sexpr w))
         ((element-type? w 'iff) (lkif-iff->sexpr w))
         ((element-type? w 'all) (lkif-all->sexpr w))
         ((element-type? w 'exists) (lkif-exists->sexpr w))
         (else (display "Error: unknown wff - ")
               (display w)
               (newline)
               '())))
 
 (define (lkif-term->sexpr t)
   (cond ((element-type? t 'v) (lkif-variable->sexpr t))
         ((element-type? t 'i) (lkif-individual->sexpr t))
         ((element-type? t 'c) (lkif-constant->sexpr t))
         ((element-type? t 'expr) (lkif-expression->sexpr t))
         ((element-type? t 's) (lkif-atom->sexpr t))
         (else (display "Error: unknown term - ")
               (display t)
               (newline)
               '())))
 
 (define (lkif-atom->sexpr a)
   (let ((pred (get-attribute-value (get-attribute a 'pred) #f))
         (assumable (get-attribute-value (get-attribute a 'assumable) "none")))
     (let ((text/term (if pred
                          (if (string=? assumable "none")
                              (list-tail a 2)
                              (list-tail a 3))
                          (if (string=? assumable "none")
                              (list-tail a 1)
                              (list-tail a 2)))) 
           (term? (element-checker 'v 'i 'c 'expr 's)))
       (call-with-values (lambda () (partition term? text/term))
                         (lambda (terms text)
                           (if (string=? assumable "true")
                               (if pred
                                   (list 'assuming (cons (string->symbol pred) (map lkif-term->sexpr terms)))
                                   (list 'assuming (string->symbol (car text))))
                               (if pred
                                   (cons (string->symbol pred) (map lkif-term->sexpr terms))
                                   (car text))))))))
 
 (define (lkif-or->sexpr o)
   (let ((assumable (get-attribute-value (get-attribute o 'assumable) #f)))
     (if assumable
         (if (string=? assumable "true")
             (list 'assuming (cons 'or (map lkif-wff->sexpr (cddr o))))
             (cons 'or (map lkif-wff->sexpr (cddr o))))
         (cons 'or (map lkif-wff->sexpr (cdr o))))))
 
 (define (lkif-and->sexpr a)
   (let ((assumable (get-attribute-value (get-attribute a 'assumable) #f)))
     (if assumable
         (if (string=? assumable "true")
             (list 'assuming (cons 'and (map lkif-wff->sexpr (cddr a))))
             (cons 'and (map lkif-wff->sexpr (cddr a))))
         (cons 'and (map lkif-wff->sexpr (cdr a))))))
 
 (define (lkif-not->sexpr n)
   (let ((exception (get-attribute-value (get-attribute n 'exception) "false"))
         (assumable (get-attribute-value (get-attribute n 'assumable) "false")))
     (if (string=? exception "true")
         (list 'unless (list 'not (lkif-wff->sexpr (car (list-tail n (- (length n) 1))))))
         (if (string=? assumable "true")
             (list 'assuming (list 'not (lkif-wff->sexpr (car (list-tail n (- (length n) 1))))))
             (list 'not (lkif-wff->sexpr (car (list-tail n (- (length n) 1)))))))))
 
 (define (lkif-if->sexpr i)
   (let ((assumable (get-attribute-value (get-attribute i 'assumable) "false")))
         (if (string=? assumable "true")
             (list 'assuming (cons 'if (map lkif-wff->sexpr (list-tail i (- (length i) 2)))))
             (cons 'if (map lkif-wff->sexpr (list-tail i (- (length i) 2)))))))
 
 (define (lkif-iff->sexpr i)
   (let ((assumable (get-attribute-value (get-attribute i 'assumable) "false")))
         (if (string=? assumable "true")
             (list 'assuming (cons 'iff (map lkif-wff->sexpr (list-tail i (- (length i) 2)))))
             (cons 'iff (map lkif-wff->sexpr (list-tail i (- (length i) 2)))))))
 
 (define (lkif-all->sexpr a)
   (display "Error: all quantifier are not supported yet - ")
   (display a)
   '())
 
 (define (lkif-exists->sexpr e)
   (display "Error: existence quantifier are not supported yet - ")
   (display e)
   '())
 
 (define (lkif-variable->sexpr v)
   (string->symbol (string-append "?" (cadr v))))
 
 (define (lkif-individual->sexpr i)
   (string->symbol (get-attribute-value (get-attribute i 'value) "i")))
 
 (define (lkif-constant->sexpr c)
   (let ((c-value (cadr c))
         (c-number (string->number (cadr c))))
     (cond (c-number c-number)
           ((string=? c-value "true") #t)
           ((string=? c-value "false") #f)
           (else (string->symbol c-value)))))
 
 (define (lkif-expression->sexpr e)
   (let ((functor (get-attribute-value (get-attribute e 'functor) "f")))
     (cons (string->symbol functor) (map lkif-term->sexpr (cddr e)))))
 
 
 ; rule conversion
 
 (define (lkif-rule->rule r)
   (let ((id (get-attribute-value (get-attribute r 'id) "r"))
         (strict (get-attribute-value (get-attribute r 'strict) "none"))
         (head (get-element r 'head))
         (body (get-element r 'body)))
     (if (string=? strict "true")
         (make-rule (string->symbol id) 
                    #t
                    (make-rule-head (cons 'and (map lkif-wff->sexpr (cdr head))))
                    (let ((to-body (if (null? body)
                                       '()
                                       (map lkif-wff->sexpr (cdr body)))))
                      (cond ((= (length to-body) 0) '())
                            ((= (length to-body) 1) (make-rule-body (car to-body)))
                            (else (make-rule-body (cons 'or to-body))))))
         (make-rule (string->symbol id) 
                    #f
                    (make-rule-head (cons 'and (map lkif-wff->sexpr (cdr head))))
                    (let ((to-body (if (null? body)
                                       '()
                                       (map lkif-wff->sexpr (cdr body)))))
                      (cond ((= (length to-body) 0) '())
                            ((= (length to-body) 1) (make-rule-body (car to-body)))
                            (else (make-rule-body (cons 'or to-body)))))))))

 
 (define (lkif-rules->generator rules qs)
   (generate-arguments-from-rules (rulebase (map lkif-rule->rule rules)) qs))
 
 
 ; axiom conversion
 
 (define (lkif-axiom->sexpr a)
   (lkif-wff->sexpr (caddr a)))
 
 (define (lkif-axioms->context a)
   (argument:accept argument:default-context (map lkif-axiom->sexpr a)))
 
 
 ; statement conversion
 
 ; insert-statement: table struct:statement -> table
 (define (insert-statement tbl s)
    (table:insert tbl (statement-id s) s))
 
 ; statements->table: (list-of struct:statement) -> table
 (define (statements->table s)
   (fold-left insert-statement (table:make-table) s))
 
 ; statement->sexpr: struct:statement -> any
 (define (statement->sexpr s)
   (lkif-atom->sexpr (statement-atom s)))
 
 ; statements->context: (list-of struct:statement) -> context
 (define (statements->context statements)
   (let ((scontext (fold-left (lambda (c s)
                                (cond 
                                  ((string=? (statement-value s) "unknown") (argument:state c (list (statement->sexpr s))))
                                  ((string=? (statement-value s) "true") (argument:accept c (list (statement->sexpr s))))
                                  ((string=? (statement-value s) "false") (argument:reject c (list (statement->sexpr s))))))
                              argument:default-context
                              statements)))
     (display scontext)
     (newline)
     (fold-left (lambda (c s)
                  (argument:assign-standard c (string->symbol (statement-standard s)) (list (statement->sexpr s))))
                scontext
                statements)))
 
 
 ; argument-graph-conversion
 
 ; lkif-argument-graph->argument-graph: struct:lkif-argument-graph -> (struct:argument-graph context)
 (define (lkif-argument-graph->argument-graph/context ag)
   (let ((statements (argument-graph-statements ag)))
     (let ((tbl (statements->table statements)))
       (let ((arguments (map (lambda (x) (argument-to-record x tbl)) (argument-graph-arguments ag))))
         (values (fold-left argument:put-argument argument:empty-argument-graph arguments)
                 (statements->context statements))))))
 
 ; argument-graph->stage: struct:lkif-argument-graph -> struct:stage
 (define (argument-graph->stage ag)
   (call-with-values (lambda () (lkif-argument-graph->argument-graph/context ag))
                     (lambda (a c)
                       (make-stage a c))))
         
 
 
 
 ; ---------------------------
 ; Testing Code

; (define doc (get-document "C:\\test2.xml"))
; 
; (define lb (get-lkif doc))
; 
; (define srcs (read-sources lb))
; 
; (define t (read-theory lb))
;  
; (define arggraphs (read-argument-graphs lb))
;
; (define rule-gen (lkif-rules->generator (theory-rules t) '()))
; 
; (define c (lkif-axioms->context (theory-axioms t)))
; 
; (define graph1 (car arggraphs))
; 
; (define stage1 (argument-graph->stage graph1))
;  
; (define tbl (statements->table (argument-graph-statements graph1)))
; 
; (define args (map (lambda (x) (argument-to-record x tbl)) (argument-graph-arguments graph1))) 
 
 (define i (import "C:\\test2.xml"))
 
 )