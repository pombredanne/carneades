#!r6rs

(import (rnrs)
        (carneades lkif2)
        (carneades argument-diagram))


(define import-data (lkif-import "q1-arguments.xml"))

(define stages (lkif-data-stages import-data))

(define st1 (car stages))

(view (stage-argument-graph st1) (stage-context st1))