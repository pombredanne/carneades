(ns carneades.maps.format-statement
  (:use carneades.engine.argument
        carneades.engine.statement
        carneades.engine.argument-evaluation)
  (:require [clojure.string :as s]))


(def max-len 30)

(defn shorten [word]
  (if (> (count word) max-len)
    (str (subs word 0 (- max-len 2)) "..")
    word))

(defn make-line [words]
  (letfn [(append
           [line word]
           (if (empty? line)
             word
             (str line " " word)))]
   (loop [taken 0
          len 0
          words words
          line ""]
     (let [word (first words)
           l (count word)]
       (cond (empty? words)
             {:words words :line line :taken taken}

             (> l max-len)
             (if (zero? taken)
               (let [word (shorten word)
                     line (append line word)]
                 {:words (rest words) :line line :taken (inc taken)
                  :last-truncated true})
               {:words words :line line :taken taken})

             (> (+ len l) max-len)
             {:words words :line line :taken taken}
            
             :else
             (recur (inc taken)
                    (+ len l)
                    (rest words)
                    (append line word)))))))


(defn trunk [s]
  (if (nil? s)
    ""
    (let [words (s/split s #"\s+")
          {words :words line1 :line} (make-line words)
          {words :words line2 :line} (make-line words)
          {words :words line3 :line last-truncated :last-truncated} (make-line words)]
      (cond (and (nil? line2) (nil? line3))
            line1

            (nil? line3)
            (str line1 "\n" line2)
            
            :else
            (str line1 "\n" line2 "\n" line3
                 (cond (and last-truncated (not (empty? words)))
                       "."

                       (not (empty? words))
                       "..."
                       
                       :else nil))))))

(defn trunk-line
  [s]
  (let [s (trunk s)
        lines (s/split-lines s)]
    (if (= (count lines) 1)
      (first lines)
      lines)))

(defn stmt-to-str [ag stmt stmt-str]
  (let [formatted (stmt-str (map->statement stmt))]
    ;; TODO: how to get the evaluation of the complement of a literal?
    ;; here stmt is a node, how to know if a StattementNode is positive?
    (cond (and (in-node? stmt) ;; (in-node? (literal-complement stmt))
               )
          (str "✔✘ " formatted)
           
          (in-node? stmt)
          (str "✔ " formatted)
           
          (in-node? stmt;; (literal-complement stmt)
                    )
          (str "✘ " formatted)

          (undecided-node? stmt)
          (str "? " formatted)
           
          :else formatted)))