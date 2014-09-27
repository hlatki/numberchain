(ns number-chain.numbers)

(def NUM_SQUARES 24)        ;; Number of squares
(def MAX_NUM_SUMMANDS 5)    ;; Maximum number of summands (must be less than
                            ;; NUM_SQUARES)
(def MAX_SUMMAND 10)        ;; Maximum value of any summand

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

(defn wrap-numbers
  "Wraps a sequence of numbers in a map with a unique id. This allows us to
   track the numbers that have been selected by id and not by their actual
   value."
  [list-of-numbers]
  (map (fn [num id] {:value num :id id}) list-of-numbers (range NUM_SQUARES)))

