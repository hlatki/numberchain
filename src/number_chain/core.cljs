(ns number-chain.core
  (:require [reagent.core :as reagent :refer [atom]]))

(def NUM_SQUARES 24)        ;; Number of squares
(def MAX_NUM_SUMMANDS 5)    ;; Maximum number of summands (must be less than
                            ;; NUM_SQUARES)
(def MAX_SUMMAND 10)        ;; Maximum value of any summand

(def app-state (atom {:app-name "Number Chain" :numbers "" :target ""}))

(defn get-element-by-id [id]
  (.getElementById js/document id))

(defn my-app []
  [:div
   [:div (str "Hello from: " (:app-name @app-state))]
   [:div (str "Number: " (:numbers @app-state))]
   [:div (str "Target: " (:target @app-state))]
   ])

(defn init! []
  (set-numbers-and-target!)
  (reagent/render-component [my-app] (get-element-by-id "app-name")))

(init!)

(comment
  (-> (generate-numbers)
      (generate-target))
  (set-numbers-and-target!)
  (js/alert "Hello Justin!"))


;; The following functions generate the numbers to populate the grid and then
;; set the appropriate atom
(defn generate-numbers
  "Helper function that generates a list containing NUM_SQUARES integers"
  []
  (take NUM_SQUARES (repeatedly #(inc (rand-int MAX_SUMMAND)))))

(defn generate-target
  "Helper function that figures out what the target should be"
  [list-of-nums]
  (let [num-summands (inc (rand-int MAX_NUM_SUMMANDS))]
    (apply + (take num-summands list-of-nums))))

(defn set-numbers-and-target!
  "Set the state of the numbers and the sum target -- call this one"
  []
  (let [num-list (generate-numbers)
        target   (generate-target num-list)]
    (swap! app-state assoc :numbers (shuffle num-list) :target target)))


