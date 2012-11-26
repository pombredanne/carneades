(ns impact.web.controllers.policy-simulation
  (:use  clojure.pprint
         clojure.data.json
         impact.web.logic.askengine
         impact.web.logic.questions
         impact.web.views.pages
         impact.web.core
         (carneades.engine policy scheme dialog unify utils)
         [carneades.engine.statement :only (neg literal-predicate variable? literal-atom variables)])
  (:require [carneades.engine.scheme :as scheme]))

(defmulti ajax-handler (fn [json _] (ffirst json)))

(def current-policy (atom 'copyright-policies))

(defn strs->stmt
  "Converts a collection of a string representing a statement on the client side
   to a formal statement."
  [coll]
  (map symbol (apply list coll)))

(defmethod ajax-handler :current-policy
  [json session]
  {:body (json-str (deref current-policy))})

;; TODO: this should be access protected
(defmethod ajax-handler :set-current-policy
  [json session]
  (reset! current-policy (symbol (:set-current-policy json)))
  {:session session})

(defmethod ajax-handler :request
  [json session]
  (prn "======================================== request handler! ==============================")
  (prn  (:lang session))
  (let [session (assoc session :query (get-main-issue (:theory session) (symbol (:request json))))
        session (ask-engine session)]
    {:session session
     :body (json-str {:questions (:last-questions session)})}))

(defn reconstruct-yesno-answer
  "Returns the statement representing the user's response for a yes/no question"
  [answer statement]
  (if (= (first (:values answer)) "yes")
    statement
    (neg statement)))

(defn reconstruct-predicate-answer
  "Returns the statement representing the user's response for a yes/no question"
  [answer statement]
  (let [vars (variables statement)
        values (map safe-read-string (:values answer))
        subs (apply hash-map (interleave vars values))]
   (apply-substitutions subs statement)))

(defn reconstruct-role-answer
  [answer statement]
  (let [[s o v] statement
        values (map safe-read-string (:values answer))]
    (list s o (first values))))

(defn reconstruct-answers
  "Reconstructs the answer from the JSON"
  [jsonanswers dialog]
  (let [theory (policies (deref current-policy))]
   (reduce (fn [questions-to-answers answer]
             (let [id (:id answer)
                   question (get-nthquestion dialog id)
                   statement (:statement question)
                   ans (cond (:yesnoquestion question)
                             (reconstruct-yesno-answer answer question)
                             (scheme/role? (get-predicate statement theory))
                             (reconstruct-role-answer answer statement)
                             :else
                             (reconstruct-predicate-answer answer statement))]
               (conj questions-to-answers [(:statement question) ans])))
           ()
           jsonanswers)))


(defmethod ajax-handler :answers
  [json session]
  (prn "======================================== answers handler! ==============================")
  (pprint json)
  (let [{:keys [last-questions dialog]} session
        questions-to-answers (reconstruct-answers (:answers json)
                                                  dialog)
        ;; _ (do (prn "[:answers] questions-to-answers =" questions-to-answers))
        session (update-in session [:dialog] add-answers questions-to-answers)
        ;; _ (do (prn "[:answers] dialog answers =" (:dialog session)))
        session (ask-engine session)]
    (if (:all-questions-answered session)
      {:session session
       :body (json-str {:solution (:solution session)
                        :db (:db session)})}
      (do
        (prn "==================== Sending question to the client ==================== ")
        (prn (json-str {:questions (:last-questions session)}))
        {:session session
         :body (json-str {:questions (:last-questions session)})}))))

(defn new-session
  []
  (prn "[new-session]")
  (prn "current-policy: " (deref current-policy))
  {:dialog (make-dialog)
   :lang "en"
   :last-id 0
   :substitutions {}
   :query nil
   :theory (policies (deref current-policy))
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
