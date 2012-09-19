(ns carneades.web.walton-schemes
  (:use (carneades.engine statement argument scheme dublin-core)))

(def walton-schemes
  (make-theory
   :header
   (make-metadata :title "Essential Walton Argumentation Schemes")

   :language
   (make-language

    (make-predicate
     :symbol 'applicable
     :arity 1
     :forms {:en (make-form
                  :positive "Rule %s is applicable."
                  :negative "Rule %s is not applicable."
                  :question "Is rule %s applicable?")})
    
    (make-predicate
     :symbol 'asserts
     :arity 2
     :forms {:en (make-form
                  :positive "%s asserts that %s is true."
                  :negative "%s does not assert that %s is true."
                  :question "Does %s assert that %s is true?")})
    

    (make-predicate
     :symbol 'based-on-evidence
     :arity 1
     :forms {:en (make-form
                  :positive "The assertion %s is based on evidence."
                  :negative "The assertion %s is not based on evidence."
                  :question "Is the assertion %s based on evidence?")})

    (make-predicate
     :symbol 'believes
     :arity 2
     :forms {:en (make-form
                  :positive "Witness %s believes %s to be true."
                  :negative "Witness %s does not believe %s to be true."
                  :question "Does witness %s believe %s to be true?")})


    (make-predicate
     :symbol 'biased
     :arity 1
     :forms {:en (make-form
                  :positive "Witness %s is biased."
                  :negative "Witness %s is not biased."
                  :question "Is witness  %s biased?")})

    (make-predicate
     :symbol 'causes
     :arity 2
     :forms {:en (make-form
                  :positive "Event %s causes event %s."
                  :negative "Event %s does not cause event %s."
                  :question "Does event %s cause event %s?")})
    
    
    (make-predicate
     :symbol 'classified-as
     :arity 2
     :forms {:en (make-form
                  :positive "Objects which satisfy definition %s are classified
                             as instances of class %s."
                  :negative "Objects which satisfy definition %s are not classified as
                             instances of class %s."
                  :question "Are objects which satisfy definition %s are classified
                             as instances of class %s?")})

    (make-predicate
     :symbol 'has-conclusion
     :arity 2
     :forms {:en (make-form
                  :positive "Rule %s has conclusion %s."
                  :negative "Rule %s does not have conclusion %s."
                  :question "Does rule %s have conclusion %s?")})

    (make-predicate
     :symbol 'correlated
     :arity 2
     :forms {:en (make-form
                  :positive "Events %s and %s are correlated."
                  :negative "Events %s and %s are not correlated."
                  :question "Are events %s and %s correlated?")})

    (make-predicate
     :symbol 'sunk-costs
     :arity 2
     :forms {:en (make-form
                  :positive "The costs incurred performing %s thus far are %s."
                  :negative "The costs incurred performing %s thus far are not %s."
                  :question "Are the costs incurred performing %s thus far %s?")})

    (make-predicate
     :symbol 'defeasibly-implies
     :arity 2
     :forms {:en (make-form
                  :positive "If %s is true then presumably %s is also true."
                  :negative "It is not the case that if %s is true then presumably %s is also true."
                  :question "Is it the case that if %s is true then %s is presumably also true?")})

    (make-predicate
     :symbol 'dishonest
     :arity 1
     :forms {:en (make-form
                  :positive "%s is dishonest."
                  :negative "%s is not dishonest."
                  :question "Is %s dishonest?")})

    (make-predicate
     :symbol 'expert
     :arity 2
     :forms {:en (make-form
                  :positive "Source %s is an expert in subject domain %s."
                  :negative "Source %s is not an expert in subject domain %s."
                  :question "Is source %s an expert in subject domain %s?")})

    (make-predicate
     :symbol 'explanation
     :arity 2
     :forms {:en (make-form
                  :positive "Theory %s explains %s."
                  :negative "Theory %s does not explain %s."
                  :question "Does theory %s explain %s?")})

    (make-predicate
     :symbol 'explanatory-theory
     :arity 3
     :forms {:en (make-form
                  :positive "%s is a theory explaining how event %s causes event %s."
                  :negative "%s is not a theory explaining how event %s causes event %s."
                  :question "Is %s an explanatory theory explaining how event %s causes event %s?")})

    (make-predicate
     :symbol 'feasible
     :arity 1
     :forms {:en (make-form
                  :positive "It is feasible to perform the action %s."
                  :negative "It is not feasible to perform the action %s."
                  :question "Is it feasible to perform the action %s?")})

    (make-predicate
     :symbol 'has-occurred
     :arity 1
     :forms {:en (make-form
                  :positive "An event %s has occurred."
                  :negative "An event %s has not occurred."
                  :question "Has an event %s occurred?")})

    (make-predicate
     :symbol 'horrible-costs
     :arity 2
     :forms {:en (make-form
                  :positive "Event %s would entail horrible costs."
                  :negative "Event %s would not entail horrible costs."
                  :question "Would event %s entail horrible costs?")})
    
    (make-predicate
     :symbol 'implausible
     :arity 1
     :forms {:en (make-form
                  :positive "%s is implausible."
                  :negative "%s is not implausible."
                  :question "Is %s implausible?")})

    (make-predicate
     :symbol 'inadequate-definition
     :arity 2
     :forms {:en (make-form
                  :positive "%s is an inadequate definition of %s."
                  :negative "%s is not an inadequate definition of %s."
                  :question "Is %s an inadequate definition of %s?")})
    
    (make-predicate
     :symbol 'inconsistent-with-facts
     :arity 1
     :forms {:en (make-form
                  :positive "%s is inconsistent with the known facts."
                  :negative "%s is not inconsistent with the known facts."
                  :question "Is %s inconsistent with the known facts?")})

    (make-predicate
     :symbol 'inconsistent-with-other-experts
     :arity 1
     :forms {:en (make-form
                  :positive "%s is inconsistent with what other experts assert."
                  :negative "%s is not inconsistent with what other experts assert."
                  :question "Is %s inconsistent with what other experts assert?")})

    (make-predicate
     :symbol 'inconsistent-with-other-witnesses
     :arity 1
     :forms {:en (make-form
                  :positive "%s is inconsistent with what other witnesses assert."
                  :negative "%s is not inconsistent with what other witnesses assert."
                  :question "Is %s inconsistent with what other witnesses assert?")})

    (make-predicate
     :symbol 'in-case
     :arity 2
     :forms {:en (make-form
                  :positive "%s is true in case %s."
                  :negative "%s is not true in case %s."
                  :question "Is %s true in case %s?")})


    (make-predicate
     :symbol 'in-domain
     :arity 2
     :forms {:en (make-form
                  :positive "%s is in domain %s."
                  :negative "%s is not in domain %s."
                  :question "Is %s in domain %s?")})

    (make-predicate
     :symbol 'instance
     :arity 2
     :forms {:en (make-form
                  :positive "%s is an instance of class %s."
                  :negative "%s is not an instance of class %s."
                  :question "Is %s an instance of class %s?")})

    (make-predicate
     :symbol 'interfere
     :arity 2
     :forms {:en (make-form
                  :positive "An event %s interfered with event %s."
                  :negative "An event %s did not interfere with event %s."
                  :question "Did an event %s interfere with event %s?")})

    (make-predicate
     :symbol 'internally-consistent
     :arity 1
     :forms {:en (make-form
                  :positive "%s is internally consistent."
                  :negative "%s is not internally consistent."
                  :question "Is %s internally consistent?")})

    (make-predicate
     :symbol 'known
     :arity 1
     :forms {:en (make-form
                  :positive "%s is known to be true."
                  :negative "%s is not known to be true."
                  :question "Is %s known to be true?")})

    (make-predicate
     :symbol 'looks-like
     :arity 2
     :forms {:en (make-form
                  :positive "%s looks like a %s."
                  :negative "%s does not look like a %s."
                  :question "Does %s look like a %s?")})

    (make-predicate
     :symbol 'member
     :arity 2
     :forms {:en (make-form
                  :positive "%s contains %s as a member."
                  :negative "%s does not contain %s as a member."
                  :question "Does %s contain %s as a member?")})

    (make-predicate
     :symbol 'more-coherent-explanation
     :arity 3
     :forms {:en (make-form
                  :positive "Theory %s is a more coherent explanation than
                             theory %s of observation %s."
                  :negative "Theory %s is not a more coherent explanation than
                             theory %s of observation %s."
                  :question "Is theory %s a more coherent explanation than
                             theory %s of observation %s?")})

    (make-predicate
     :symbol 'more-on-point
     :arity 3
     :forms {:en (make-form
                  :positive "%s is false in case %s, which is more on point that case %s."
                  :negative "%s is not false in case %s, or that case is not more on point than case %s."
                  :question "Is %s false in case %s, which is more on point than case %s?")})

    (make-predicate
     :symbol 'negative-consequences
     :arity 1
     :forms {:en (make-form
                  :positive "Performing action %s would have negative consequences."
                  :negative "Performing action %s would not have negative consequences."
                  :question "Would performing action %s have negative consequences?")})

    (make-predicate
     :symbol 'observation
     :arity 1
     :forms {:en (make-form
                  :positive "%s has been observed."
                  :negative "%s has not been observed."
                  :question "Has %s been observed?")})

    (make-predicate
     :symbol 'position-to-know
     :arity 2
     :forms {:en (make-form
                  :positive "%s is in a position to know about things in a certain subject domain %s."
                  :negative "%s is not in a position to know about things in a certain subject domain %s."
                  :question "Is %s in a position to know about things in a certain subject domain %s?")})

    (make-predicate
     :symbol 'positive-consequences
     :arity 1
     :forms {:en (make-form
                  :positive "Performing action %s would have positive consequences."
                  :negative "Performing action %s would not have positive consequences."
                  :question "Would performing action %s have positive consequences?")})

    (make-predicate
     :symbol 'relevant-differences
     :arity 1
     :forms {:en (make-form
                  :positive "There are relevant differences between case %s and the current case."
                  :negative "There are no relevant differences between case %s and the current case."
                  :question "Are there relevant differences between case %s and the current case?")})

    (make-predicate
     :symbol 'satisfies-definition
     :arity 2
     :forms {:en (make-form
                  :positive "%s satisfies definition %s."
                  :negative "%s does not satisfy definition %s."
                  :question "Does %s satisfy definition %s?")})
    
    (make-predicate
     :symbol 'similar-case
     :arity 1
     :forms {:en (make-form
                  :positive "Case %s is similar to the current case."
                  :negative "Case %s is not similar to the current case."
                  :question "Is case %s similar to the current case?")})

    (make-predicate
     :symbol 'subclass
     :arity 2
     :forms {:en (make-form
                  :positive "%s is a subclass of %s."
                  :negative "%s is not a subclass of %s."
                  :question "Is %s a subclass of %s?")})

    (make-predicate
     :symbol 'too-high-to-waste
     :arity 1
     :forms {:en (make-form
                  :positive "The sunk costs of %s are too high to waste."
                  :negative "The sunk costs of %s are not too high to waste."
                  :question "Are the sunk costs of %s too high to waste?")})

    (make-predicate
     :symbol 'trustworthy
     :arity 1
     :forms {:en (make-form
                  :positive "%s is trustworthy."
                  :negative "%s is not trustworthy."
                  :question "Is %s trustworthy?")})

    (make-predicate
     :symbol 'uninvestigated
     :arity 1
     :forms {:en (make-form
                  :positive "The truth of the statement %s has been investigated."
                  :negative "The truth of the statement %s has not been investigated."
                  :question "Has the truth of the statement %s been investigated?")})
    
    (make-predicate
     :symbol 'untrustworthy
     :arity 1
     :forms {:en (make-form
                  :positive "%s is personally reliable as a source."
                  :negative "%s is not personally reliable as a source."
                  :question "Is %s personally reliable as a source?")})

    (make-predicate
     :symbol 'valid
     :arity 1
     :forms {:en (make-form
                  :positive "Rule %s is valid."
                  :negative "Rule %s is not valid."
                  :question "Is rule %s valid?")})

    (make-predicate
     :symbol 'worthy-goal
     :arity 1
     :forms {:en (make-form
                  :positive "%s is a worthy goal."
                  :negative "%s is not a worthy goal."
                  :question "Is %s a worthy goal?")})

    (make-predicate
     :symbol 'would-achieve
     :arity 2
     :forms {:en (make-form
                  :positive "Performing action %s would achieve goal %s."
                  :negative "Performing action %s would not achieve goal %s."
                  :question "Would performing action %s achieve goal %s?")})

    (make-predicate
     :symbol 'would-be-known
     :arity 1
     :forms {:en (make-form
                  :positive "%s would be known if it were true."
                  :negative "%s might not be known even if it is true."
                  :question "Would %s be known if it were true?")})
    
    
    (make-predicate
     :symbol 'would-demote-value
     :arity 2
     :forms {:en (make-form
                  :positive "Achieving the goal %s would demote the value %s."
                  :negative "Achieving the goal %s would note demote the value %s."
                  :question "Would achieving the goal %s demote the value %s")})

    
    (make-predicate
     :symbol 'would-promote-value
     :arity 2
     :forms {:en (make-form
                  :positive "Achieving the goal %s would promote the value %s."
                  :negative "Achieving the goal %s would not promote the value %s."
                  :question "Would achieving the goal %s promote the value %s")})

    (make-predicate
     :symbol 'would-realize
     :arity 2
     :forms {:en (make-form
                  :positive "Performing %s would realize event %s."
                  :negative "Performing %s would not realize event %s."
                  :question "Would performing %s realize event %s?")})

    ) ;; end of language

   :schemes
   [(make-scheme
     :id 'position-to-know
     :header (make-metadata
              :title "Argument from Position to Know"
              :source "Douglas Walton, Legal Argumentation and Evidence, The Pennsylvania State University Press, University Park, 2002, p.46.")
     :conclusion '?A
     :premises [(make-premise
                 :role "major"
                 :statement '(position-to-know ?W ?D))
                (make-premise
                 :role "minor"
                 :statement '(asserts ?W ?S))
                (make-premise
                 :role "domain"
                 :statement '(in-domain ?S ?D))]
     ;; Critical Questions
     :exceptions [(make-premise
                   :role "CQ1"
                   :statement '(dishonest ?W))])

    (make-scheme
     :id 'witness-testimony
     :header (make-metadata
              :title "Argument from Witness Testimony"
              :source "Douglas Walton, Henry Prakken, Chris Reed, Argumentation Schemes and Generalisations in Reasoning about Evidence, Proceedings of the 9th International Conference on Artificial Intelligence and Law, Edinburgh, 2003. New York: ACM Press 2003, pp. 35.
Douglas Walton, Witness Testimony Evidence, unpublished book manuscript, to appear.")
     :conclusion '?A
     :premises [(make-premise
                 :role "position to know"
                 :statement '(position-to-know ?W ?A))
                (make-premise
                 :role "truth telling"
                 :statement '(believes ?W ?A))
                (make-premise
                 :role "minor"
                 :statement '(asserts ?W ?A))]
     ;; Critical Questions
     :exceptions [(make-premise
                   :role "CQ2"
                   :statement '(inconsistent-with-facts ?A))
                  (make-premise
                   :role "CQ3"
                   :statement '(inconsistent-with-other-witnesses ?A))
                  (make-premise
                   :role "CQ4"
                   :statement '(biased ?W))
                  (make-premise
                   :role "CQ5"
                   :statement '(implausible ?A))]
     :assumptions [(make-premise
                    :role "CQ1"
                    :statement '(internally-consistent ?A))])

    (make-scheme
     :id 'expert-opinion
     :header (make-metadata
              :title "Argument from Expert Opinion"
              :source "Douglas Walton, Legal Argumentation and Evidence, The Pennsylvania State University Press, University Park, 2002, pp.49-50.
Douglas Walton, Appeal to Expert Opinion, The Pennsylvania University Press, University Park, Albany, 1997, p.211-225.")
     :conclusion '?A
     :premises [(make-premise
                 :role "major"
                 :statement '(expert ?E ?S))
                (make-premise
                 :role "domain"
                 :statement '(in-domain ?A ?S))
                (make-premise
                 :role "minor"
                 :statement '(asserts ?E ?A))]
     ;; Critical Questions
     :exceptions [(make-premise
                   :role "CQ4"
                   :statement '(untrustworthy ?E))
                  (make-premise
                   :role "CQ5"
                   :statement '(inconsistent-with-other-experts ?A))]
     :assumptions [(make-premise
                    :role "CQ6"
                    :statement '(based-on-evidence ?A))])

    (make-scheme
     :id 'analogy
     :header (make-metadata
              :title "Argument from Analogy"
              :source "Douglas Walton, Fundamentals of Critical Argumentation, Cambridge University Press, New York 2006, p. 96-97.")
     :conclusion '?S
     :premises [(make-premise
                 :role "major"
                 :statement '(similar-case ?C1))
                (make-premise
                 :role "case"
                 :statement '(in-case ?S ?C1))
                (make-premise
                 :role "minor"
                 :statement '(asserts ?E ?A))]
     ;; Critical Questions
     :exceptions [(make-premise
                   :role "CQ1"
                   :statement '(relevant-differences ?C1))
                  (make-premise
                   :role "CQ2"
                   :statement '(more-on-point ?S ?C2 ?C1))])

    (make-scheme
     :id 'precedent
     :header (make-metadata
              :title "Argument from Precedent"
              :source "Douglas Walton, A Pragmatic Theory of Fallacy, The University of Alabama Press, Tuscaloosa and London, 1995, p. 148.")
     :conclusion '?S
     :premises [(make-premise
                 :role "major"
                 :statement '(similar-case ?C1))
                (make-premise 
                 :role "ratio"
                 :statement '(rule-of-case ?C1 ?R))
                (make-premise
                 :role "conclusion"
                 :statement '(has-conclusion ?R ?S))]
     ;; Critical Questions
     :exceptions [(make-premise
                   :role "CQ2"
                   :statement '(relevant-differences ?C1))
                  (make-premise
                   :role "CQ3"
                   :statement '(inapplicable-rule ?R))])

    (make-scheme
     :id 'verbal-classification
     :header (make-metadata :title "Argument from Verbal Classification")
     :strict true
     :conclusion '(instance ?O ?G)
     :premises [(make-premise
                 :role "individual"
                 :statement '(instance ?O ?F))
                (make-premise 
                 :role "classification"
                 :statement '(subclass ?F ?G))])

    (make-scheme
     :id 'definition-to-verbal-classification
     :header (make-metadata
              :title "Argument from Definition to Verbal Classification"
              :source "Douglas Walton, Fundamentals of Critical Argumentation, Cambridge University Press, New York 2006, p. 129.")
     :conclusion '(instance ?O ?G)
     :premises [(make-premise
                 :role "definition"
                 :statement '(satisfies-definition ?O ?D))
                (make-premise
                 :role "classification"
                 :statement '(classified-as ?D ?G))]
     :exceptions [(make-premise
                   :role "CQ1"
                   :statement '(inadequate-definition ?D ?G))])

    (make-scheme
     :id 'defeasible-modus-ponens
     :header (make-metadata :title "Defeasible Modus Ponens")
     :conclusion '?B
     :premises [(make-premise
                 :role "major"
                 :statement '(defeasibly-implies ?A ?B))
                (make-premise
                 :role "minor"
                 :statement '?A)])

    (make-scheme
     :id 'established-rule
     :header (make-metadata
              :title "Argument from an Established Rule"
              :source "Douglas Walton, A Pragmatic Theory of Fallacy, The University of Alabama Press, Tuscaloosa and London, 1995, p. 147.")
     :conclusion '?C
     :premises [(make-premise
                 :role "major"
                 :statement '(has-conclusion ?R ?C))
                (make-premise
                 :role "minor"
                 :statement '(applicable ?R))]
     :assumptions [(make-premise
                    :role "validity"
                    :statement '(valid ?R))])
    
    (make-scheme
     :id 'value-promotion
     :header (make-metadata :title "Argument from Value Promotion")
     :conclusion '(worthy-goal ?G)
     :premises [(make-premise
                 :role "major"
                 :statement '(would-promote-value ?G ?V))])

    (make-scheme
     :id 'value-demotion
     :header (make-metadata :title "Argument from Value Demotion")
     :conclusion '(not (worthy-goal ?G))
     :premises [(make-premise
                 :role "major"
                 :statement '(would-demote-value ?G ?V))])

    (make-scheme
     :id 'positive-consequences
     :header (make-metadata
              :title "Argument from Positive Consequences"
              :source "Douglas Walton, A Pragmatic Theory of Fallacy, The University of Alabama Press, Tuscaloosa and London, 1995, pp. 155-156.
Douglas Walton, Scare Tactics, Kluwer Academic Publishers, Dordrecht, 2000, p.123.")
     :conclusion '(should-be-performed ?A)
     :premises [(make-premise
                 :role "major"
                 :statement '(positive-consequences ?A))])
    
    (make-scheme
     :id 'negative-consequences
     :header (make-metadata
              :title "Argument from Negative Consequences"
              :source "Douglas Walton, A Pragmatic Theory of Fallacy, The University of Alabama Press, Tuscaloosa and London, 1995, pp. 155-156.
Douglas Walton, Scare Tactics, Kluwer Academic Publishers, Dordrecht, 2000, p.123.")
     :conclusion '(not (should-be-performed ?A))
     :premises [(make-premise
                 :role "major"
                 :statement '(negative-consequences ?A))])

    (make-scheme
     :id 'practical-reasoning
     :header (make-metadata
              :title "Argument from Practical Reasoning"
              :source "Trevor Bench-Capon, ‘Persuasion in Practical Argument Using Value-based Argumentation Frameworks’, Journal of Logic and Computation, 13, 2003, 429-448.
Douglas Walton, Practical Reasoning, Rowman and Littlefield, Savage, Maryland, 1990.
Douglas Walton, ‘Actions and Inconsistency: the Closure Problem of Practical Reasoning’, Contemporary Action Theory, ed. Ghita Holmstrom-Hintikka and Raimo Tuomela, Vol. 1, Dordrecht, Kluwer, 1997, p. 164.
Douglas Walton, Slippery Slope Arguments, Vale Press, Newport News, 1999 pp.89-90.")
     :conclusion '(should-be-performed ?A)
     :premises [(make-premise
                 :role "goal"
                 :statement '(worthy-goal ?G))
                (make-premise
                 :role "action"
                 :statement '(would-achieve ?A ?G))])

    (make-scheme
     :id 'cause-to-effect
     :header (make-metadata :title "Argument from Cause to Effect.")
     :conclusion '(will-occur ?E2)
     :premises [(make-premise
                 :role "minor"
                 :statement '(has-occurred ?E1))
                (make-premise
                 :role "major"
                 :statement '(causes ?E1 ?E2))]
     :exceptions [(make-premise
                   :role "CQ1"
                   :statement '(interfere ?E3 ?E1))])

    (make-scheme
     :id 'correlation-to-cause
     :header (make-metadata
              :title "Argument from Correlation to Cause"
              :source "Douglas Walton, A Pragmatic Theory of Fallacy, The University of Alabama Press, Tuscaloosa and London, 1995, p. 142.
Douglas Walton, Fundamentals of Critical Argumentation, Cambridge University Press, New York 2006, p. 101-103.")
     :conclusion '(causes ?E1 ?E2)
     :premises [(make-premise
                 :role "major"
                 :statement '(correlated ?E1 ?E2))]
     :exceptions [(make-premise
                   :role "CQ3"
                   :statement '(causes ?E3 (and ?E1 E2)))]
     :assumptions [(make-premise
                    :role "CQ2"
                    :statement '(explanatory-theory ?T ?E1 ?E2))])

    (make-scheme
     :id 'sunk-costs 
     :header (make-metadata
              :title "Argument from Sunk Costs"
              :source "Douglas Walton, ‘The Sunk Costs Fallacy or Argument from Waste’, Argumentation, 16, 2002, p. 489.")
     :conclusion '(should-be-performed ?A)
     :premises [(make-premise
                 :role "costs"
                 :statement '(sunk-costs ?A ?C))
                (make-premise
                 :role "waste"
                 :statement '(too-high-to-waste ?C))]
     :assumptions [(make-premise
                    :role "CQ1"
                    :statement '(feasible ?A))])

    (make-scheme
     :id 'appearance
     :header (make-metadata
              :title "Argument from Appearance"
              :source "Douglas Walton, ‘Argument from Appearance: A New Argumentation Scheme’, 2006.")
     :conclusion '(instance ?O ?C)
     :premises [(make-premise
                 :role "minor"
                 :statement '(looks-like ?O ?C))])

    (make-scheme
     :id 'ignorance
     :header (make-metadata
              :title "Argument from Ignorance"
              :source "Douglas Walton, Arguments from Ignorance, The Pennsylvania University Press, University Park, Albany, 1996, pp.84, 86.
Douglas Walton, A Pragmatic Theory of Fallacy, The University of Alabama Press, Tuscaloosa and London, 1995, p. 150.")
     :conclusion '(not ?S)
     :premises [(make-premise
                 :role "major"
                 :statement '(would-be-known ?S))
                (make-premise
                 :role "minor"
                 :statement '(not (known ?S)))]
     :exceptions [(make-premise
                   :role "CQ1"
                   :statement '(uninvestigated ?S))])

    (make-scheme
     :id 'abduction
     :header (make-metadata :title "Argument from Abduction")
     :conclusion '?H
     :premises [(make-premise
                 :role "observation"
                 :statement '(observed ?S))
                (make-premise
                 :role "explanation"
                 :statement '(explanation ?T1 ?S))
                (make-premise
                 :role "hypothesis"
                 :statement '(member ?T1 ?H))]
     :exceptions [(make-premise
                   :role "CQ1"
                   :statement '(more-coherent-explanation ?T2 ?T1 ?S))])

    (make-scheme
     :id 'ethotic
     :header (make-metadata
              :title "Ethotic Argument"
              :source "Douglas Walton, A Pragmatic Theory of Fallacy, The University of Alabama Press, Tuscaloosa and London, 1995, p. 152.")
     :conclusion '?S
     :premises [(make-premise
                 :role "assertion"
                 :statement '(asserts ?P ?S))
                (make-premise
                 :role "trustworthiness"
                 :statement '(trustworthy ?P))])

    ;; to do: add negative version of ethotic arguments, for untrustworthy pesrons

    (make-scheme
     :id 'slippery-slope
     :header (make-metadata
              :title "Slippery Slope Argument"
              :source "Douglas Walton, Slippery Slope Arguments, Vale Press, Newport News, 1999, pp. 93, 95.
Douglas Walton, Fundamentals of Critical Argumentation, Cambridge University Press, New York 2006, pp. 107, 110.")
     :conclusion '(not (should-be-performed ?A))
     :premises [(make-premise
                 :role "realization"
                 :statement '(would-realize ?A ?E1))
                (make-premise
                 :role "causality"
                 :statement '(causes ?E1 ?E2))
                (make-premise
                 :role "horrible costs"
                 :statement '(horrible-costs ?E2))])

    (make-scheme
     :id 'transitivity-of-causality1
     :header (make-metadata
              :title "Transitivity of Causality, Base Case"
              :source "Douglas Walton, Slippery Slope Arguments, Vale Press, Newport News, 1999, pp. 93, 95.
Douglas Walton, Fundamentals of Critical Argumentation, Cambridge University Press, New York 2006, pp. 107, 110.")
     :conclusion '(causes ?E1 ?E1))

    (make-scheme
     :id 'transitivity-of-causality
     :header (make-metadata
              :title "Transitivity of Causality, Inductive Step"
              :source "Douglas Walton, Slippery Slope Arguments, Vale Press, Newport News, 1999, pp. 93, 95.
Douglas Walton, Fundamentals of Critical Argumentation, Cambridge University Press, New York 2006, pp. 107, 110."
              :description {:en "__Here__ some description of the scheme will be written"})
     :conclusion '(causes ?E1 ?E2)
     :premises [(make-premise
                 :role "step 1"
                 :statement '(causes ?E1 ?E3))
                (make-premise
                 :role "step 2"
                 :statement '(causes ?E3 ?E2))])
    
    ])) ;; end of theory of Walton's schemes

(def schemes-by-predicate
  (create-scheme-predicate-index {} walton-schemes))

(def schemes-by-id
  (create-scheme-id-index {} walton-schemes))





