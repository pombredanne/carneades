;;; Copyright (c) 2012 Fraunhofer Gesellschaft
;;; Licensed under the EUPL V.1.1

(ns impact.web.controllers.policy-simulation
  (:use [clojure.data.json :only [json-str read-json]]
        [clojure.tools.logging :only [info debug error]]
        [carneades.engine.policy :only [get-main-issue policies]]
        [carneades.engine.utils :only [safe-read-string exists?]]
        [impact.web.logic.askengine :only [start-engine send-answers-to-engine]]
        [impact.web.logic.questions :only [get-predicate get-questions-for-answers-modification
                                           modify-statements]]
        [impact.web.views.pages :only [index-page config-page]]
        [carneades.engine.statement :only [neg literal-predicate variable? literal-atom variables]]
        [carneades.engine.unify :only [apply-substitutions]]
        [carneades.engine.dialog :only [get-nthquestion add-answers]]
        [ring.util.codec :only [base64-decode]])
  (:require [carneades.engine.scheme :as scheme]
            [carneades.database.db :as db]
            [carneades.database.admin :as admin]
            [clojure.string :as str]))

(defmulti ajax-handler (fn [json _ _] (ffirst json)))

(def current-policy (atom 'copyright-policies))

(defn strs->stmt
  "Converts a collection of a string representing a statement on the client side
   to a formal statement."
  [coll]
  (map symbol (apply list coll)))

(defmethod ajax-handler :current-policy
  [json session request]
  {:body (json-str (deref current-policy))})

;; TODO: this should be access protected
(defmethod ajax-handler :set-current-policy
  [json session request]
  (reset! current-policy (symbol (:set-current-policy json)))
  {:session session})

(defmethod ajax-handler :request
  [json session request]
  (debug "======================================== request handler! ==============================")
  (let [session (assoc session :query (get-main-issue (:theory session) (symbol (:request json))))
        session (start-engine session)]
    {:session session
     :body (json-str {:questions (:last-questions session)})}))

(defn reconstruct-yesno-answer
  "Returns the vector representing the user's response for a yes/no question"
  [values statement]
  (let [value (condp = (first values)
                "yes" 1.0
                "no" 0.0
                "maybe" 0.5)]
   [statement value]))

(defn reconstruct-predicate-answer
  "Returns the vector representing the user's response for a predicate"
  [values statement]
  (let [vars (variables statement) 
        subs (apply hash-map (interleave vars values))]
   [(apply-substitutions subs statement) 1.0]))

(defn reconstruct-role-answer
  [values statement]
  (let [[s o v] statement
        value (first values)]
    [(list s o (symbol (first values))) 1.0]))

(defn reconstruct-answer
  [question theory values]
  (let [statement (:statement question)]
   (cond (:yesnoquestion question)
         (reconstruct-yesno-answer values statement)
         (scheme/role? (get-predicate statement theory))
         (reconstruct-role-answer values statement)
         :else
         (reconstruct-predicate-answer values statement))))

(defn reconstruct-answers
  "Reconstructs the answer from the JSON"
  [jsonanswers dialog]
  (let [theory (policies (deref current-policy))]
   (reduce (fn [questions-to-weight answer]
             (let [id (:id answer)
                   values (:values answer)
                   question (get-nthquestion dialog id)
                   ans (reconstruct-answer question theory values)
                   [statement value] ans]
               (assoc questions-to-weight statement value)))
           {}
           jsonanswers)))

(defmethod ajax-handler :answers
  [json session request]
  (debug "======================================== answers handler! ==============================")
  (debug json)
  (let [{:keys [last-questions dialog]} session
        questions-to-answers (reconstruct-answers (:answers json)
                                                  dialog)
        ;; _ (do (prn "[:answers] questions-to-answers =" questions-to-answers))
        session (update-in session [:dialog] add-answers questions-to-answers)
        ;; _ (do (prn "[:answers] dialog answers =" (:dialog session)))
        session (send-answers-to-engine session)]
    (if (:all-questions-answered session)
      {:session session
       :body (json-str {:solution (:solution session)
                        :db (:db session)})}
      {:session session
       :body (json-str {:questions (:last-questions session)})})))

(defmethod ajax-handler :modifiable-facts
  [json session request]
  (let [jsondata (json :modifiable-facts)
        db (jsondata :db)
        theory (jsondata :theory)]
    {:body (json-str (get-questions-for-answers-modification
                      db
                      theory
                      (keyword (:lang session))))}))

(defn array->stmt
  ([a acc]
     (if (coll? a)
       (cons (array->stmt (first a)) (map array->stmt (rest a)))
       (symbol a)))
  ([a]
     (array->stmt a ())))

(defn reconstruct-statement
  [fact]
  (assoc fact
    :statement (array->stmt (:statement fact))))

(defn reconstruct-statements
  [facts]
  (map reconstruct-statement facts))

(defn get-username-and-password
  [request]
  (prn "REQUEST =")
  (prn request)
  (prn)
  (let [authorization (second (str/split (get-in request [:headers "authorization"]) #" +"))
        authdata (String. (base64-decode authorization))]
    (str/split authdata #":")))

(defmethod ajax-handler :modify-facts
  [json session request]
  (let [data (:modify-facts json)
        facts (:facts data)
        db (:db data)
        theory (policies (deref current-policy))
        facts (reconstruct-statements facts)
        to-modify (map (fn [q] (reconstruct-answer q theory (:values q))) facts)
        [username password] (get-username-and-password request)]
    (modify-statements db "root" "pw1" to-modify theory)
    {:body ""}))

(defn new-session
  [lang]
  {:pre [(not (nil? lang))]}
  (info "[new-session] lang =" lang)
  (info "current-policy: " (deref current-policy))
  {:lang lang
   :query nil
   :theory (policies (deref current-policy))})

(defmethod ajax-handler :reset
  [json session request]
  (info "[reset] json=" json)
  {:session (new-session (get-in json [:reset :lang]))})

(defmethod ajax-handler :lang
  [json session request]
  (info "[lang] new language is" (:lang json))
  {:session (assoc session :lang (:lang json))})

(defn process-ajax-request
  [request]
  (let [{:keys [session body params]} request
        json (read-json (slurp body))
        _ (info "JSON =")
        _ (info json)
        res (ajax-handler json session request)]
    res))

(defn init-page
  []
  (info "init of session")
  {:headers {"Content-Type" "text/html;charset=UTF-8"}
   :session (new-session "en")
   :body (index-page)})

(defn dump-config
  []
  (config-page))

(defn init-debate-db
  []
  (when (not (exists? (db/dbfilename "debates")))
    (admin/create-debate-database "debates" "root" "pw1")
    (db/with-db (db/make-database-connection "debates" "root" "pw1")
      (admin/create-debate {:title "Copyright in the Knowledge Economy"
                            :public true
                            :id "copyright"}))))

(init-debate-db)
