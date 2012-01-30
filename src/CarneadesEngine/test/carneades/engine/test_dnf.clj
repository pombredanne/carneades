;;; Copyright © 2010-2012 Fraunhofer Gesellschaft 
;;; Licensed under the EUPL V.1.1

(ns carneades.engine.test-dnf
  (:use clojure.test
        carneades.engine.dnf))

(def f0 'a)
(def f1 '(not a))
(def f2 '(not a b))
(def f3 '(not (and a b)))
(def f4 '(and (not a) b))
(def f5 '(or a b))
(def f6 '(or a (and b c)))
(def f7 '(or a (and (not b) c)))
(def f8 '(and a (and b c)))
(def f9 '(or (not a) (and a b) (and c d)))
(def f10 '(or a b c (not d)))
(def f11 '(or a b c (not (and a b))))
(def f12 '(or (not (not a)) (not (and (not b) (not c))) (not (and a b))))
(def f13 '(not (and a (assuming b) (unless c) (or d (assuming e)))))
(def f14 '(and a b
                   (and c (or d (or (assuming f) g)) h)
                   (or (not (and (unless i) j (and (not k) l))) m)))
(def f15 '(if (and a (or b (iff c d))) e))
(def f16 '(or (and a (if b (and c d))) e))
(def f17 '(or (and a (or b (and c d))) e))
(def f18 '(iff (and a (or b (if c d))) e))

(deftest test-atom?
  (is (atom? f0))
  (is (not (atom? f1)))
  (is (not (atom? f2)))
  (is (not (atom? f3)))
  (is (not (atom? f4)))
  (is (not (atom? f5)))
  (is (not (atom? f6)))
  (is (not (atom? f7)))
  (is (not (atom? f8)))
  (is (not (atom? f9)))
  (is (not (atom? f10)))
  (is (not (atom? f11)))
  (is (not (atom? f12)))
  (is (not (atom? f13)))
  (is (not (atom? f14)))
  (is (not (atom? f15)))
  (is (not (atom? f16)))
  (is (not (atom? f17)))
  (is (not (atom? f18))))

(deftest test-literal?
  (is (literal? f0))
  (is (literal? f1))
  (is (not (literal? f2)))
  (is (not (literal? f3)))
  (is (not (literal? f4)))
  (is (not (literal? f5)))
  (is (not (literal? f6)))
  (is (not (literal? f7)))
  (is (not (literal? f8)))
  (is (not (literal? f9)))
  (is (not (literal? f10)))
  (is (not (literal? f11)))
  (is (not (literal? f12)))
  (is (not (literal? f13)))
  (is (not (literal? f14)))
  (is (not (literal? f15)))
  (is (not (literal? f16)))
  (is (not (literal? f17)))
  (is (not (literal? f18))))

(deftest test-extliteral?
  (is (extliteral? f0))
  (is (extliteral? f1))
  (is (not (extliteral? f2)))
  (is (not (extliteral? f3)))
  (is (not (extliteral? f4)))
  (is (not (extliteral? f5)))
  (is (not (extliteral? f6)))
  (is (not (extliteral? f7)))
  (is (not (extliteral? f8)))
  (is (not (extliteral? f9)))
  (is (not (extliteral? f10)))
  (is (not (extliteral? f11)))
  (is (not (extliteral? f12)))
  (is (not (extliteral? f13)))
  (is (not (extliteral? f14)))
  (is (not (extliteral? f15)))
  (is (not (extliteral? f16)))
  (is (not (extliteral? f16))))

(deftest test-negation?
  (is (not (negation? f0)))
  (is (negation? f1))
  (is (not (negation? f2)))
  (is (negation? f3))
  (is (not (negation? f4)))
  (is (not (negation? f5)))
  (is (not (negation? f6)))
  (is (not (negation? f7)))
  (is (not (negation? f8)))
  (is (not (negation? f9)))
  (is (not (negation? f10)))
  (is (not (negation? f11)))
  (is (not (negation? f12)))
  (is (negation? f13))
  (is (not (negation? f14)))
  (is (not (negation? f15)))
  (is (not (negation? f16)))
  (is (not (negation? f17)))
  (is (not (negation? f18))))

(deftest test-conjunction?
  (is (not (conjunction? f0)))
  (is (not (conjunction? f1)))
  (is (not (conjunction? f2)))
  (is (not (conjunction? f3)))
  (is (conjunction? f4))
  (is (not (conjunction? f5)))
  (is (not (conjunction? f6)))
  (is (not (conjunction? f7)))
  (is (conjunction? f8))
  (is (not (conjunction? f9)))
  (is (not (conjunction? f10)))
  (is (not (conjunction? f11)))
  (is (not (conjunction? f12)))
  (is (not (conjunction? f13)))
  (is (conjunction? f14))
  (is (not (conjunction? f15)))
  (is (not (conjunction? f16)))
  (is (not (conjunction? f17)))
  (is (not (conjunction? f18))))

(deftest test-lconjunction?
  (is (lconjunction? f0))
  (is (lconjunction? f1))
  (is (not (lconjunction? f2)))
  (is (not (lconjunction? f3)))
  (is (lconjunction? f4))
  (is (not (lconjunction? f5)))
  (is (not (lconjunction? f6)))
  (is (not (lconjunction? f7)))
  (is (not (lconjunction? f8)))
  (is (not (lconjunction? f9)))
  (is (not (lconjunction? f10)))
  (is (not (lconjunction? f11)))
  (is (not (lconjunction? f12)))
  (is (not (lconjunction? f13)))
  (is (not (lconjunction? f14)))
  (is (not (lconjunction? f15)))
  (is (not (lconjunction? f16)))
  (is (not (lconjunction? f17)))
  (is (not (lconjunction? f18))))

(deftest test-disjunction?
  (is (not (disjunction? f0)))
  (is (not (disjunction? f1)))
  (is (not (disjunction? f2)))
  (is (not (disjunction? f3)))
  (is (not (disjunction? f4)))
  (is (disjunction? f5))
  (is (disjunction? f6))
  (is (disjunction? f7))
  (is (not (disjunction? f8)))
  (is (disjunction? f9))
  (is (disjunction? f10))
  (is (disjunction? f11))
  (is (disjunction? f12))
  (is (not (disjunction? f13)))
  (is (not (disjunction? f14)))
  (is (not (disjunction? f15)))
  (is (disjunction? f16))
  (is (disjunction? f17))
  (is (not (disjunction? f18))))

(deftest test-implication?
  (is (not (implication? f0)))
  (is (not (implication? f1)))
  (is (not (implication? f2)))
  (is (not (implication? f3)))
  (is (not (implication? f4)))
  (is (not (implication? f5)))
  (is (not (implication? f6)))
  (is (not (implication? f7)))
  (is (not (implication? f8)))
  (is (not (implication? f9)))
  (is (not (implication? f10)))
  (is (not (implication? f11)))
  (is (not (implication? f12)))
  (is (not (implication? f13)))
  (is (not (implication? f14)))
  (is (implication? f15))
  (is (not (implication? f16)))
  (is (not (implication? f17)))
  (is (not (implication? f18))))

(deftest test-equivalence?
  (is (not (equivalence? f0)))
  (is (not (equivalence? f1)))
  (is (not (equivalence? f2)))
  (is (not (equivalence? f3)))
  (is (not (equivalence? f4)))
  (is (not (equivalence? f5)))
  (is (not (equivalence? f6)))
  (is (not (equivalence? f7)))
  (is (not (equivalence? f8)))
  (is (not (equivalence? f9)))
  (is (not (equivalence? f10)))
  (is (not (equivalence? f11)))
  (is (not (equivalence? f12)))
  (is (not (equivalence? f13)))
  (is (not (equivalence? f14)))
  (is (not (equivalence? f15)))
  (is (not (equivalence? f16)))
  (is (not (equivalence? f17)))
  (is (equivalence? f18)))

(deftest test-dnf?
  (is (dnf? f0))
  (is (dnf? f1))
  (is (not (dnf? f2)))
  (is (not (dnf? f3)))
  (is (dnf? f4))
  (is (dnf? f5))
  (is (dnf? f6))
  (is (dnf? f7))
  (is (not (dnf? f8)))
  (is (dnf? f9))
  (is (dnf? f10))
  (is (not (dnf? f11)))
  (is (not (dnf? f12)))
  (is (not (dnf? f13)))
  (is (not (dnf? f14)))
  (is (not (dnf? f15)))
  (is (not (dnf? f16)))
  (is (not (dnf? f17)))
  (is (not (dnf? f18))))

(deftest test-formula?
  (is (formula? f0))
  (is (formula? f1))
  (is (not (formula? f2)))
  (is (formula? f3))
  (is (formula? f4))
  (is (formula? f5))
  (is (formula? f6))
  (is (formula? f7))
  (is (formula? f8))
  (is (formula? f9))
  (is (formula? f10))
  (is (formula? f11))
  (is (formula? f12))
  (is (formula? f13))
  (is (formula? f14))
  (is (formula? f15))
  (is (formula? f16))
  (is (formula? f17))
  (is (formula? f18)))

(deftest test-distri
  (is (= (distri '(a b c) '(or d e f)) '(or (and d a b c)
                                            (and e a b c)
                                            (and f a b c)))))

(deftest test-con-flatten
  (is (con-flatten '((and a b) (and b (and (or cc dd) d) d) (or d e)))
      '(a b b (or cc dd) d d (or d e))))

(deftest test-to-dnf
  (is (= (to-dnf f0) 'a))
  (is (= (to-dnf f1) '(not a)))
  (is (= (to-dnf f3) '(or (not a) (not b))))
  (is (= (to-dnf f4) '(and (not a) b)))
  (is (= (to-dnf f5) '(or a b)))
  (is (= (to-dnf f6) '(or a (and b c))))
  (is (= (to-dnf f7) '(or a (and (not b) c))))
  (is (= (to-dnf f8) '(and a b c)))
  (is (= (to-dnf f9) '(or (not a) (and a b) (and c d))))
  (is (= (to-dnf f10) '(or a b c (not d))))
  (is (= (to-dnf f11) '(or a b c (not a) (not b))))
  (is (= (to-dnf f12) '(or a b c (not a) (not b))))
  (is (= (to-dnf f13) '(or (not a) (assuming (not b)) (unless (not c))
                           (and (not d) (assuming (not e))))))
  (is (= (to-dnf f14) '(or
                        (and (unless (not i)) d (a b c h))
                        (and (not j) d (a b c h))
                        (and k d (a b c h))
                        (and (not l) d (a b c h))
                        (and m d (a b c h))
                        (and (unless (not i)) (assuming f) (a b c h))
                        (and (not j) (assuming f) (a b c h))
                        (and k (assuming f) (a b c h))
                        (and (not l) (assuming f) (a b c h))
                        (and m (assuming f) (a b c h))
                        (and (unless (not i)) g (a b c h))
                        (and (not j) g (a b c h))
                        (and k g (a b c h))
                        (and (not l) g (a b c h))
                        (and m g (a b c h)))))
  (is (= (to-dnf f15) '(or (not a) (and c (not d) (not b))
                          (and d (not c) (not b)) e)))
  (is (= (to-dnf f16) '(or (and (not b) a) (and c d a) e)))
  (is (= (to-dnf f17) '(or (and b a) (and c d a) e)))
  (is (= (to-dnf f18) '(or
                        (and (not a) ()
                             (or (not e) (or (and b a)
                                             (and (not c) a)
                                             (and d a))))
                        (and (and (not b) c (not d)) () (or (not e)
                                                            (or (and b a)
                                                                (and (not c) a)
                                                                (and d a))))
                        (and e () (or (not e) (or (and b a) (and (not c) a)
                                                  (and d a))))))))
