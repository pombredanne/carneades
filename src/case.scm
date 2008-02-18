(module case mzscheme
  
  ; generator for arguments from cases
  
  (require "stream.scm")
  (require "argument.scm")
  (require "argument-search.scm")
  (require "statement.scm")
  (require "unify.scm")
  (require (lib "match.ss"))
  (require (prefix list: (lib "list.ss" "srfi" "1")))
  (require (prefix set: (planet "set.ss" ("soegaard" "galore.plt" 3))))
  
  (provide (all-defined))
  
  (define *debug* #f)
  
  ; TO DO: critical questions, e.g. about the applicability of the precedent due to, e.g.
  ; being from another jurisdiction.
  
  ;; type party = 'plaintiff | 'defendant
  
  (define (other-party party)
    (case party
      ((defendant) 'plaintiff)
      ((plaintiff) 'defendant)
      (else (error "not a party" party))))
  
  (define-struct factor 
    (statement   ; the statement expressed by the factor
     favors      ; a party
     parent))    ; a factor or #f if there is none    
  
  ;; type factors = (set-of factor)
  
  ; make-factors: (list-of factor) -> factors
  (define (make-factors factors) (set:list->eq factors))
  
  (define-struct %case
    (name        ; string, "current" if the current case
     pfactors    ; a set of factors
     dfactors    ; a set of factors
     winner))    ; the winning party or 'undecided, if the current case
  
  ; make-case: string party (list-of factors) -> case
  (define (make-case name winner factors)
    (let-values (((pfactors dfactors) 
                  (list:partition (lambda (f) (eq? (factor-favors f) 'plaintiff)) factors)))
      (make-%case name (set:list->eq pfactors) (set:list->eq dfactors) winner)))
  
  (define case? %case?)
  (define case-name %case-name)
  (define case-pfactors %case-pfactors)
  (define case-dfactors %case-dfactors)
  (define case-winner %case-winner)
  
  ; case-factors: case -> (set-of factors)
  ; the union of the pfactors and dfactors of the case
  (define (case-factors c)
    (set:union (case-pfactors c) (case-dfactors c)))
  
  ; case-statement: case -> (list-of statement)
  (define (case-statements c)
    (map factor-statement (set:elements (case-factors c))))
  
  (define-struct %casebase
    (issue      ; a statement, the plaintiff's claim
     factors    ; the sef of all factors in the casebase
     cases))    ; a set of cases
  
  (define casebase? %casebase?)
  
  ; make-casebase: statement (list-of factors) (list-of case) -> casebase
  (define (make-casebase issue factors cases)
    (make-%casebase issue 
                    (set:list->eq factors) 
                    (set:list->eq cases)))
  
  ; casebase-issue: casebase -> statement
  (define (casebase-issue cb) (%casebase-issue cb))
  
  ; casebase-factors: casebase -> (set-of factors)
  (define (casebase-factors cb) (%casebase-factors cb))
  
  ; casebase-cases: casebase -> (set-of case)
  (define (casebase-cases cb) (%casebase-cases cb))
  
  ; list-cases: casebase -> (list-of case)
  (define (list-cases cb) (set:elements (%casebase-cases cb)))
  
  ; get-case: casebase string
  ; Retrieves a case in the case base with the given name.  
  ; If there is more than one case with this name, one is picked nondeterminstically.
  ; Returns #f if there is no case with this name.
  (define (get-case cb name)
    (let ((s (set:filter (lambda (c) 
                           (equal? (case-name c) name)) 
                         (casebase-cases cb))))
      (if (set:empty? s)
          #f
          (set:select s))))
  
  ;; case comparison partitions
  
  ; partition1: case case -> (set-of factors)
  ; plaintiff factors in common
  (define (partition1 cc pc)
    (set:intersection (case-pfactors cc) (case-pfactors pc)))
  
  ; partition2: case case -> (set-of factors)
  ; defendant factors in common
  (define (partition2 cc pc)
    (set:intersection (case-dfactors cc) (case-dfactors pc)))
  
  ; partition3: case case -> (set-of factors)
  ; plaintiff factors in cc but not in pc
  (define (partition3 cc pc)
    (set:difference (case-pfactors cc) (case-pfactors pc)))
  
  ; partition4: case case -> (set-of factors)
  ; defendant factors in pc but not in cc
  (define (partition4 cc pc)
    (set:difference (case-dfactors pc) (case-dfactors cc)))
  
  ; partition5: case case -> (set-of factors)
  ; defendant factors in cc but not in pc
  (define (partition5 cc pc)
    (set:difference (case-dfactors cc) (case-dfactors pc)))
  
  ; partition6: case case -> (set-of factors)
  ; plaintiff factors in pc but not in cc
  (define (partition6 cc pc)
    (set:difference (case-pfactors pc) (case-pfactors cc)))
  
  ; partition7: casebase case case -> (set-of factors)
  ; factors of the casebase which are not in either the cc or the pc
  (define (partition7 cb cc pc)
    (set:difference (casebase-factors cb)
                    (set:union (case-factors cc) 
                               (case-factors pc))))
  
  
  ; more-on-point: case case case -> (set-of factor)
  ; Check if pc1 is more on point than pc2, relative to cc
  ; pc1 is more on point than pc2 only the factors of pc1 are a *proper* superset
  ; of the factors of pc2.  If pc1 is more on point, the factors
  ; in pc1 which are not pc2 are returned.  If pc1 is not more on point,
  ; the empty set is returned.
  (define (more-on-point pc1 pc2 cc)
    (let* ((p1-pc1 (partition1 cc pc1))  ; pfactors in common with pc1
           (p1-pc2 (partition1 cc pc2))   ; pfactors in common with pc2
           (p2-pc1 (partition2 cc pc1))   ; dfactors in common with pc1
           (p2-pc2 (partition2 cc pc2))   ; dfactors in common with pc2
           (s1 (set:union p1-pc1 p2-pc1))
           (s2 (set:union p1-pc2 p2-pc2)))
      (if (and (set:subset? s2 s1)
               (not (set:subset? s1 s2)))
          (set:difference s1 s2)
          ; else return an empty set
          (set:make-eq))))
  
  ; as-point-point: case case case -> (set-of factor)
  ; same as more-on-point, except the factors of pc1 need only to be a superset,
  ; but not a proper superset, of the factors of pc2.
  (define (as-on-point pc1 pc2 cc)
    (let* ((p1-pc1 (partition1 cc pc1))  ; pfactors in common with pc1
           (p1-pc2 (partition1 cc pc2))   ; pfactors in common with pc2
           (p2-pc1 (partition2 cc pc1))   ; dfactors in common with pc1
           (p2-pc2 (partition2 cc pc2))   ; dfactors in common with pc2
           (s1 (set:union p1-pc1 p2-pc1))
           (s2 (set:union p1-pc2 p2-pc2)))
      (if (set:subset? s2 s1)
          (set:difference s1 s2)
          ; else return an empty set
          (set:make-eq))))
  
  ; NOTE: The code for common-ancestor below is useless, since it ignores reverses in the party
  ; favored by a factor, from child to parent
  
  ;  ; ancestors: factor -> (set-of factor)
  ;  ; WARNING: Assumes the factor hierarchy is acyclic.  May not 
  ;  ; terminate if cycles exist.
  ;  (define (ancestors f)
  ;    (let ((parent (factor-parent f)))
  ;      (if parent
  ;          (set:union (set:make-eq parent) (ancestors parent))
  ;          ; else the empty set
  ;          (set:list->eq null)))) 
  ;  
  ;  ; common-ancestor?: factor factor -> boolean
  ;  (define (common-ancestor? f1 f2)
  ;    (not (set:empty? (set:intersection (ancestors f1) (ancestors f2)))))
  ;  
  ;  
  ;  ; factor-with-common-ancestor: factor case -> factor | #f
  ;  ; Returns a factor in a case which has an ancestor in common with f1, or #f if there
  ;  ; is no such factor in the case.
  ;  (define (factor-with-common-ancestor f1 case)
  ;    (let ((candidates (set:filter (lambda (f2) (common-ancestor? f1 f2))
  ;                                  (case-factors case))))
  ;      (if (set:empty? candidates)
  ;          #f
  ;          (set:select candidates))))
  ;  
  ;  ; common-ancestor: factor case -> factor | #f
  ;  ; Returns a common ancestor of f1 and some factor in the given case, or #f if there
  ;  ; is no such factor
  ;  (define (common-ancestor f1 case)
  ;    (let ((f2 (factor-with-common-ancestor f1 case)))
  ;      (if (not f2)
  ;          #f ; there is no factor in the case with an ancestor in common with some ancestor of f1
  ;          (let ((candidates (set:intersection (ancestors f1) (ancestors f2))))
  ;            (if (set:empty? candidates) ; should never be the case
  ;                #f
  ;                (set:select candidates))))))
  
  (define (common-parent? f1 f2)
    (and (factor-parent f1)
         (factor-parent f2)
         (eq? (factor-parent f1)
              (factor-parent f2))))
  
  ; decided-for-other-party: case case -> boolean
  (define (decided-for-other-party pc1 pc2)
    (cond ((eq? (case-winner pc1) 'plaintiff)
           (eq? (case-winner pc2) 'defendant))
          ((eq? (case-winner pc1) 'defendant)
           (eq? (case-winner pc2) 'plaintiff))
          (else #f))) ; to handle undecided cases
  
  ; current-case: state casebase -> case
  ; The factors of the current case are the factors in the casebase which 
  ; have been accepted or are acceptable in the argument graph of the state
  (define (current-case state cb)
    (let ((ag (state-arguments state))
          (c (state-context state)))
      (make-case "current" 
                 'undecided 
                 (set:elements (set:filter (lambda (factor)
                                             (or (accepted? c (factor-statement factor))
                                                 (acceptable? ag c (factor-statement factor))))
                                           (casebase-factors cb))))))
  
  
  ; type generator: statement state  -> (stream-of response)
  
  ; generate-arguments-from-cases: casebase -> generator
  (define (generate-arguments-from-cases cb)
    (lambda (goal state)
      (let ((subs (state-substitutions state))
            (args (state-arguments state)))
      ; dispatch: statement casebase  -> generator | #f
      (if *debug* (printf "cbr debug: goal is ~v~n" goal))
      (match goal
        (('factors-favor p i)
         ; scheme: cite on-point precedent 
         ; AS2 ("preference from precedent") of [Wyner & Bench-Capon, 2007]
         (if *debug* (printf "cbr debug, factors-favor~n"))
         (let ((party (subs p))
               (issue (subs i)))
           ; (printf "debug: factors-favor ~v ~v~n" party issue)
           (if (not (ground? `(factors-favor ,party ,issue)))
               (stream) ; fail
               (stream-flatmap 
                (lambda (precedent)
                  (let* ((cc (current-case state cb))
                         (common-factors 
                          (set:union (partition1 cc precedent)    ; factors in common for plaintiff
                                     (partition2 cc precedent)))  ; factors in common for defendant
                         (scheme (string-append "AS2. cite: \"" (case-name precedent) "\""))
                         (previous-schemes (schemes-applied (state-arguments state) 
                                                            (statement-atom goal))))
                    (if (and (not (set:empty? common-factors))
                             (eq? (case-winner precedent) party)
                             (not (member scheme previous-schemes))) 
                        ; cc and pc have pfactors in common and pc was decided in favor of plaintiff
                        (stream (make-response
                                 subs ; no new subs
                                 (make-argument
                                  ; id
                                  (gensym 'a)
                                  ; direction:
                                  'pro
                                  ; conclusion: 
                                  goal
                                  ; premises
                                  (append (map pr 
                                               (map factor-statement 
                                                    (set:elements common-factors)))
                                          
                                          ; exceptions
                                          (map ex 
                                               (list `(has-counterexample ,(other-party party) ,(case-name precedent))
                                                     `(distinguishable ,(other-party party) ,(case-name precedent))
                                                     )))
                                  ; scheme:
                                  scheme)))
                        ; else fail
                        (stream))))
                (apply stream (set:elements (casebase-cases cb))))))) 
        
        (('has-counterexample p cn)
                 
         ; scheme: cite an more-on-point counterexample to the cited case, i.e. a precedent
         ; with more pfactors and dfactors in common with cc than the cited case that went the other
         ; way. 
         
         (if *debug* (printf "cbr debug, has-counterexample~n"))

         (let* ((party (subs p))
                (cname (subs cn))
                (previous-precedent (get-case cb cname)))
           (if (or (not (ground? `(has-counterexample ,party ,cname)))
                   (not previous-precedent))
               (stream)
               (let ((cc (current-case state cb))) ; the current case
                 (stream-flatmap 
                  (lambda (new-precedent)
                    (let* ((diff (more-on-point new-precedent previous-precedent cc))
                           (scheme (string-append "counterexample: \"" (case-name new-precedent) "\""))
                           (previous-schemes (schemes-applied (state-arguments state)
                                                              (statement-atom goal))))
                      (if (and (not (set:empty? diff))
                               (decided-for-other-party new-precedent previous-precedent)
                               (not (member scheme previous-schemes)))
                          (stream (make-response 
                                   subs ; no new subs
                                   (make-argument
                                    ; id
                                    (gensym 'a)
                                    ; direction
                                    'pro 
                                    ; conclusion:
                                    goal
                                    ; premises 
                                    (append 
                                     ; ordinary premises
                                     (map pr (map factor-statement (set:elements diff)))
                                     ; exceptions
                                     (map ex (list  `(has-counterexample ,(other-party party) ,(case-name new-precedent))
                                                    `(distinguishable ,(other-party party) ,(case-name new-precedent))
                                                    )))
                                    ; scheme: 
                                    scheme)))
                          ; else fail
                          (stream))))
                  (apply stream (set:elements (casebase-cases cb)))))))) 
        
        (('distinguishable p cn)
         ; scheme: distinguish with pfactors in PC not in CC
         ; this is AS3 and AS4 of [Wyner & Bench-Capon, 2007] generalized to handle
         ; arguments for defedant as well as plaintiff. The scheme looks for 
         ; arguments favoring the given party.
         
         (if *debug* (printf "cbr debug, distinguishable~n"))
                  
         (let* ((party (subs p))
                (cname (subs cn))
                (pc (get-case cb cname)))
           (if (or (not (ground? `(distinguishable ,party ,cname)))
                   (not pc))
               (stream) ; fail, no precedent with this name
               (let* ((cc (current-case state cb))
                      (p3 (partition3 cc pc)) ; pfactors in cc but not in pc
                      (p4 (partition4 cc pc)) ; dfactors in pc but not in cc
                      (p5 (partition5 cc pc)) ; dfactors in cc but not in pc
                      (p6 (partition6 cc pc)) ; pfactors in pc but not in cc
                      (weaker-cc-factors 
                       (case party
                         ((plaintiff) p3)
                         ((defendant) p5)  
                         (else (error "not a party" party))))
                      (stronger-pc-factors 
                       (case party 
                         ((plaintiff) p4) 
                         ((defendant) p6) 
                         (else (error "not a party" party))))) 
                 (stream-append 
                  ; AS3: PC Stronger
                  (let* ((scheme "AS3. precedent stronger")
                         (previous-schemes (schemes-applied (state-arguments state) 
                                                            (statement-atom goal))))
                    (if (or (set:empty? stronger-pc-factors)
                            (member scheme previous-schemes))
                        (stream) ; fail, the precedent is not distinguishable
                        ; else 
                        (stream (make-response
                                 subs ; no new subs
                                 (make-argument
                                  ; id
                                  (gensym 'a)
                                  ; direction
                                  'pro
                                  ; conclusion:
                                  goal
                                  ; premises
                                  (map ex (append (map factor-statement (set:elements stronger-pc-factors))
                                                  (list `(downplay precedent-stronger ,(other-party party) ,cname))))
                                  ; scheme: 
                                  scheme)))))
                  ; AS4: CC Weaker
                  (let* ((scheme "AS4. current case weaker")
                         (previous-schemes (schemes-applied (state-arguments state)
                                                            (statement-atom goal))))
                    (if (or (set:empty? weaker-cc-factors)
                            (member scheme previous-schemes))
                        (stream) ; fail, the precedent is not distinguishable
                        ; else 
                        (stream (make-response 
                                 subs ; no new subs
                                 (make-argument
                                  ; id
                                  (gensym 'a)
                                  ; direction
                                  'pro
                                  ; conclusion:
                                  goal
                                  ; premises
                                  (append 
                                   ; ordinary premises 
                                   (map pr (map factor-statement (set:elements weaker-cc-factors)))
                                   ; exceptions
                                   (list (ex `(downplay current-case-weaker ,(other-party party) ,cname))))
                                  
                                  ; scheme: 
                                  scheme))))))
                 ))))
       
        
        (('downplay dt p cn)
         ; scheme: the distinguishing factors of the cc can be explained away by using the factor hierarchy to
         ; substitute or cancel factors. See [Wyner & Bench-Capon, 2007].
         ; distinction-type: 'precedent-stronger | 'current-case-weaker
         
         (if *debug* (printf "cbr debug, downplay~n"))

                  
         (let* ((distinction-type (subs dt))
                (party (subs p))
                (cname (subs cn))
                (pc (get-case cb cname))) ; get the precedent case
           (if (or (not (ground? `(downplay ,distinction-type ,party ,cname)))
                   (not pc))
               (stream) ; fail, no precedent with this name
               (let* ((cc (current-case state cb))
                      (base-partition 
                       (case distinction-type 
                         ((current-case-weaker) (partition5 cc pc))
                         ((precedent-stronger) (partition6 cc pc))
                         (else (error "Not a distinction type:" distinction-type))))
                      (p3 (partition3 cc pc)) ; p factors in cc not in pc
                      (p4 (partition4 cc pc)) ; d factors in pc not in cc
                      (cancelling-partition 
                       (case distinction-type
                         ((current-case-weaker) p3)
                         ((precedent-stronger) p4)
                         (else (error "Not a distinction type:" distinction-type))))
                      (substituting-partition 
                       (case distinction-type
                         ((current-case-weaker) p4)
                         ((precedent-stronger) p3)
                         (else (error "Not a distinction type:" distinction-type))))
                      (p3-downplayed 
                       (set:filter (lambda (p3-factor)
                                     (set:any? (lambda (base-factor) 
                                                 (common-parent? base-factor p3-factor))
                                               base-partition))
                                   p3))
                      (p4-downplayed 
                       (set:filter (lambda (p4-factor)
                                     (set:any? (lambda (base-factor) 
                                                 (common-parent? base-factor p4-factor))
                                               base-partition))
                                   p4))
                      (cancelling (or (and (eq? distinction-type 'precedent-stronger)
                                           (not (set:empty? p4-downplayed)))
                                      (and (eq? distinction-type 'current-case-weaker)
                                           (not (set:empty? p3-downplayed)))))
                      (substituting (or (and (eq? distinction-type 'precedent-stronger)
                                             (not (set:empty? p3-downplayed)))
                                        (and (eq? distinction-type 'current-case-weaker)
                                             (not (set:empty? p4-downplayed)))))
                      (scheme "downplay")
                      (previous-schemes (schemes-applied (state-arguments state)
                                                         (statement-atom goal))))
                 (if (and (not (set:empty? (set:union p3-downplayed p4-downplayed)))
                          (not (member scheme previous-schemes)))
                     (stream (make-response 
                              subs ; no new subs
                              (make-argument 
                               ; id:
                               (gensym 'a)
                               ; direction:
                               'pro
                               ; conclusion:
                               goal
                               ; premises -- the downplayed factors
                               (map pr (append (map factor-statement (set:elements p3-downplayed))
                                               (map statement-complement 
                                                    (map factor-statement 
                                                         (set:elements p4-downplayed)))))
                               ; scheme: 
                               scheme)))
                     ; else fail
                     (stream))))))
        
        ; else handle goals about the issue of the case base
        (_ (match goal 
             (('not statement)
              
              (if *debug* (printf "cbr debug, AS1, negative goal~v~n" (subs goal)))

              (let* ((scheme (string-append "AS1. factor comparison"))
                     (previous-schemes (schemes-applied (state-arguments state) 
                                                        (statement-atom goal))))
                ; complementary version of AS1 for negated goals
                (if (and (eq? (subs statement)
                              (casebase-issue cb))
                         (not (member scheme previous-schemes)))
                    (stream (make-response 
                             subs ; no new subs
                             (make-argument 
                              ; id:
                              (gensym 'a)
                              ; direction:
                              'con
                              ; conclusion:
                              statement
                              ; premises 
                              (list (pr `(factors-favor defendant ,(casebase-issue cb))))
                              ; scheme: 
                              scheme)))
                    (stream)))) ; fail
             (_ (if (eq? (subs goal)
                         (casebase-issue cb))
                    ; scheme: factor comparison
                    ; AS1 ("main scheme") of [Wyner & Bench-Capon, 2007]
                    ; Note: it is assumed that factors of the current case needed for comparing the case
                    ; with precedents are acceptable in the argument graph.  One way to do this is to first query
                    ; the user, using the evidence module. Alternatively, the factors can be accepted in the 
                    ; argument context. Deriving the factors using rules or other schemes will also work, so long
                    ; as care is taken to assure the required factors are acceptable in the argument graph 
                    (let* ((scheme (string-append "AS1. factor comparison"))
                           (previous-schemes (schemes-applied (state-arguments state) 
                                                              (statement-atom goal))))
                      (if *debug* (printf "cbr debug, AS1, positive goal ~v~n" (subs goal)))
                      (if (and (eq? (subs goal)
                                    (casebase-issue cb))
                               (not (member scheme previous-schemes)))
                          ; before using the CBR schemes.
                          (begin
                            ; (printf "cbrd debug, AS1 success~n")
                            (stream (make-response 
                                     subs ; no new subs
                                     (make-argument 
                                      ; id:
                                      (gensym 'a)
                                      ; direction:
                                      'pro
                                      ; conclusion:
                                      goal
                                      ; premises 
                                      (list (pr `(factors-favor plaintiff ,(casebase-issue cb))))
                                      ; scheme: 
                                      scheme))))
                          (begin
                            ; (printf "cbr debug, AS1 fail~n")
                            (stream))))
                    (begin 
                      (if *debug* (printf "cbr debug, AS1, goal ~v does not equal issue ~v~n" 
                                          (subs goal) 
                                          (casebase-issue cb)))
                      (stream)))))) ; fail
        )))) ; end of generate-arguments-from-cases
  
  ) ; end of case-based reasoning module