(ns impact.web.controllers.policy-simulation
  (:use  clojure.pprint
         clojure.data.json
         impact.web.logic.askengine
         impact.web.logic.statement-translation
         impact.web.views.pages
         impact.web.core
         (carneades.engine policy scheme dialog unify)
         [carneades.engine.statement :only (neg literal-predicate variable? literal-atom variables)]))

(defmulti ajax-handler (fn [json _] (ffirst json)))

(def impact-theory (load-theory impact-policies-file
                        (symbol impact-policies-namespace)
                        (symbol impact-policies-name)))

(defn strs->stmt
  "Converts a collection of a string representing a statement on the client side
   to a formal statement."
  [coll]
  (map symbol (apply list coll)))

(defmethod ajax-handler :request
  [json session]
  (prn "======================================== request handler! ==============================")
  (prn  (:lang session))
  (let [session (assoc session :query (get-main-issue (:theory session) (symbol (:request json))))
        session (ask-engine session)]
    {:session session
     :body (json-str {:questions (:last-questions session)})}))

(defn reconstruct-answers-from-json
  [jsonanswers dialog]
  (map (fn [answer]
         (let [id (:id answer)
               question (get-nthquestion dialog id)
               atomic-question (:statement question)
               _ (prn "[reconstruct-answers-from-json]" answer)
               vars (variables atomic-question)
               values (map symbol (:values answer))
               subs (apply hash-map (interleave vars values))
               ans (if (zero? (:arity question))
                         ;; TODO translation!
                     (if (= (first (:values answer)) "yes") atomic-question (neg atomic-question))
                     ;; else
                     (apply-substitutions subs atomic-question))]
           (prn "[reconstruct-answers-from-json]" ans)
           ans))
       jsonanswers))


(defmethod ajax-handler :answers
  [json session]
  (prn "======================================== answers handler! ==============================")
  (pprint json)
  (let [answers (reconstruct-answers-from-json (-> json :answers :values)
                                               (:dialog session))
        session (update-in session [:dialog] add-answers answers)
        session (ask-engine session)]
    (if (:all-questions-answered session)
      {:session session
       :body (json-str {:solution (:solution session)
                        :db (:db session)})}
      {:session session
       :body (json-str {:questions (:last-questions session)})})))

(defn new-session
  []
  {:dialog (make-dialog)
   :lang "en"
   :last-id 0
   :substitutions {}
   :query nil
   :theory impact-theory
   :askables nil
   :engine-runs false})

(defmethod ajax-handler :reset
  [json session]
  (prn "[policy-simulation] resetting session")
  {:session (new-session)})

(defn process-ajax-request
  [session body params]
  (let [json (read-json (slurp body))
        res (ajax-handler json session)]
    res))


(defn init-page
  []
  (prn "init of session")
  {:headers {"Content-Type" "text/html;charset=UTF-8"}
   :session (new-session)
   :body (index-page)})

(defn dump-config
  []
  (config-page))
