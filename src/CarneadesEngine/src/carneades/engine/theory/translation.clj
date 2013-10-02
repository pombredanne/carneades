(ns ^{:doc "Translation from a language."}
  carneades.engine.theory.translation
  (:require [clojure.pprint :refer [pprint]]
            [carneades.engine.argument-graph :as ag]
            [carneades.engine.theory :as t]
            [carneades.engine.theory.namespace :as n]
            [carneades.engine.statement :as st]
            [carneades.engine.translation :as tr]))

(declare format-literal-args)

(defn format-literal-arg
  "Format the argument of a literal."
  [arg language lang]
  (cond (and (symbol? arg) (language arg))
        (or (get-in language [arg :text lang])
            (get-in language [arg :text :en]))

        (and (st/literal? arg) (language (st/literal-predicate (st/literal-atom arg))))
        (let [pred (st/literal-atom (st/literal-predicate arg))
              fstring (or (get-in language [pred :text lang])
                          (get-in language [pred :text :en]))]
          (apply format fstring (format-literal-args arg language lang)))

        :else (str arg)))

(defn format-literal-args
  "Format the arguments of a literal"
  [literal language lang]
  (map #(format-literal-arg % language lang) (rest (st/literal-atom literal))))

(defn has-translation?
  [language pred lang]
  (and (language pred)
       (get-in language [pred :forms lang])))

(defn make-language-translator
  "Returns a translator translating literals with the help of forms
  defined in a language."
  [language]
  (fn [context]
    (let [atom (or (:virtual-atom context) (st/literal-atom (:literal context)))
          pred (st/literal-predicate (:literal context))
          lang (tr/get-lang context)]
      (if (has-translation? language pred lang)
        (let [fstring (get-in language [pred :forms lang (context :direction :positive)])
              translation (apply format fstring (format-literal-args atom language lang))]
          (assoc context :translation translation))
        context))))

(defn make-uri-shortening-translator
  "Returns a translator shortening the URI with namespaces"
  [namespaces]
  (fn [context]
    (let [atom (or (:virtual-atom context) (st/literal-atom (:literal context)))
          short-atom (n/to-relative-atom atom namespaces)]
      (assoc context :virtual-atom short-atom))))
