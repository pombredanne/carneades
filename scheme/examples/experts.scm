#!r6rs

(import (except (rnrs) assert)
        (carneades argument)
        (carneades argument-diagram))


(define s "S")

(define s1
  "E1 says S is true.") 

(define s2
  "E1 is an expert in 
the domain of S.")

(define s3
  "E2 says S is true.")

(define s4
  "E2 is an expert in 
the domain of S.")

(define s5
  "E1 says E2 is not trustworthy 
because he is biased.")

(define s6
  "E2 says E1 is not trustworthy 
because he is biased.")

(define s7
  "E1 is not trustworthy.")

(define s8
  "E2 is not trustworthy.")


(define-argument a1 (pro s (pr s1) (am s2) (ex s7)))
(define-argument a2 (pro s (pr s3) (am s4) (ex s8)))
(define-argument a3 (pro s7 (pr s6) (ex s8)))
(define-argument a4 (pro s8 (pr s5) #;(ex s7)))

(define ag1 (assert empty-argument-graph (list a1 a2 a3 a4)))
(define c1 (accept default-context (list s1 s2 s3 s4)))
(define c2 (accept c1 (list s1 s2 s3 s4 s5)))
(define c3 (accept c2 (list s1 s2 s3 s4 s5 s6)))


(view ag1 c3)