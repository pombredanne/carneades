#!r6rs

(import (except (rnrs base) assert)
        (prefix (rnrs lists) list:)
        (rnrs hashtables)
        (prefix (carneades lkif) lkif:)
        (carneades statement)
        (carneades argument)
        (carneades argument-diagram))


(define imports (lkif:import "ITpilot2_3.xml"))
(define texts (index-by-statement (list:filter text? imports)))

(define ag1 (car (list:filter argument-graph? imports)))

(define c1 (accept default-context '()))
; (define c2 (reject c1 'email))

(define (show ag context)
  (view* ag 
         context 
         (lambda (x) x)
         (lambda (s) 
           (let ((txt (hashtable-ref texts s #f)))
             (if (and txt (not (equal? (text-summary txt) "")))
                 (text-summary txt)
                 s)))))

(show ag1 c1)
; (show ag1 c2)