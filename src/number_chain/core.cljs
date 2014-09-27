(ns number-chain.core
  (:require [reagent.core :as reagent :refer [atom]]
            [number-chain.numbers :refer [generate-numbers generate-target wrap-numbers]]))

;; For now, this is our initial game state. On init! we will reset our app-state atom back to this.
(def initial-game-state {:app-name "Number Chain"
                         :numbers {}
                         :target nil
                         :selected #{}})

(def app-state (atom initial-game-state))

(defn get-element-by-id [id]
  (.getElementById js/document id))

(defn my-app
  "For now, this will display the current fields in our game state.
   Nice for debugging/visualizing the app-state."
  []
  [:div
   [:div (str "Hello from: " (:app-name @app-state))]
   [:div (str "Number: " (:numbers @app-state))]
   [:div (str "Target: " (:target @app-state))]
   [:div (str "Selected " (:selected @app-state))]
   ])

(defn check-win
  "Check our win conditition. Sum up the currently selected cells in our grid
   and compare to the :target in our app-state. Currently it also resets the
   game by calling init!"
  []
  (let [selected-nums (map
                        #(:value %)
                        (filter
                          #((:selected @app-state) (% :id))
                          (:numbers @app-state)))
        sum (apply + selected-nums)]
    (when (= sum (:target @app-state))
      (js/alert "You won!")
      (init!))))

(defn toggle-selected
  "Called when a click is recieved on one of our game cells. Adds or removes
   the numbers :id to the :selected app-state set, then checks the win
   condition."
  [e]
  (let [target (-> e (.-currentTarget) (.getAttribute "data-id") int)]
    (if (get (:selected @app-state) target)
      (swap! app-state assoc :selected (disj (:selected @app-state) target))
      (swap! app-state assoc :selected (conj (:selected @app-state) target)))
    (check-win)))

(defn small-cell
  "Takes a single value from the :numbers app-state value and produces the cell
   that represents that number. Properly sets the bg color and data-id tag
   fields."
  [{:keys [value id]}]
  [:div.col-md-3 {:style {:border "1px solid #d3d3d3"
                          :background (if ((:selected @app-state) id)
                                        "#999999"
                                        "#aaaaaa")}
                  :data-id id
                  :on-click toggle-selected}
   value])

; We could clean this up, but it should work for now.
(defn build-game
  "Reagent component that contains our game grid."
  []
  (let [numbers (:numbers @app-state)]
    [:div.container 
     (into [:div.row] (for [x (range 0 4)]
                        (small-cell (nth numbers x))))
     (into [:div.row] (for [x (range 4 8)]
                       (small-cell (nth numbers x))))
     (into [:div.row] (for [x (range 8 12)]
                        (small-cell (nth numbers x))))
     (into [:div.row] (for [x (range 12 16)]
                        (small-cell (nth numbers x))))
     (into [:div.row] (for [x (range 16 20)]
                        (small-cell (nth numbers x))))
     (into [:div.row] (for [x (range 20 24)]
                        (small-cell (nth numbers x))))
     ]))

(defn set-numbers-and-target!
  "Set the state of the numbers and the sum target -- call this one"
  []
  (let [num-list (generate-numbers)
        target   (generate-target num-list)]
    (swap! app-state assoc :numbers (wrap-numbers (shuffle num-list)) :target target)))

(defn init! []
  (reset! app-state initial-game-state)
  (set-numbers-and-target!)
  (reagent/render-component [my-app] (get-element-by-id "app-name"))
  (reagent/render-component [build-game] (get-element-by-id "game")))

(init!)

(comment
  (-> (generate-numbers)
      (generate-target))
  (set-numbers-and-target!)
  (js/alert "Hello Justin!"))


