(ns number-chain.score
  (:require [reagent.core :refer [atom]]
            [clojure.string :as string]))

(def score (atom 0))

(defn load-high-score
  "Load the high score from the a browser cookie. Return zero if unsuccessful."
  []
  (if-let [val (-> (.-cookie js/document) (string/split  #"=") second int)]
    val
    0))

(def high-score (atom (load-high-score)))

(defn save-high-score! [score]
  "Save the high score in a browser cookie."
  (set! (.-cookie js/document) (str "score=" score)))

(defn score-component
  "Reagent component for the current score and high score."
  []
  [:div
   [:div.score (str "Score " @score)]
   [:div.high-score (str "High Score " @high-score)]])
