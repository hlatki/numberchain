(ns number-chain.core
  (:require [reagent.core :as reagent :refer [atom]]
            [number-chain.numbers :refer [generate-numbers generate-target wrap-numbers]]
            [number-chain.timer :refer [start-timer! timer-component count-down timer-state stop-timer!]]
            [number-chain.score :refer [score high-score save-high-score! load-high-score score-component]]))

(declare init!)
;; For now, this is our initial game state. On init! we will reset our app-state atom back to this.
(def initial-game-state {:app-name "Number Chain"
                         :numbers {}
                         :target nil
                         :selected #{}})

;; We need the symbols defined in this ns to be called when the countdown runs out.
(swap! timer-state assoc :time-up-fn (fn [] (js/alert "You lost!") (reset! score 0) (init!))) 

(def app-state (atom initial-game-state))

(defn get-element-by-id [id]
  (.getElementById js/document id))

(defn get-elements-by-class-name [cn]
  (.getElementsByClassName js/document cn))

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
      (reset! score (+ @score (* (count selected-nums) 10)))
      (when (> @score @high-score)
        (reset! high-score @score)
        (save-high-score! @score))
      (stop-timer!)
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

(def div-grid-col :div.col-md-3.col-xs-3.grid-cell)
(def div-grid-container :div.container)
(def div-grid-row :div.row)

(defn small-cell
  "Takes a single value from the :numbers app-state value and produces the cell
   that represents that number. Properly sets the bg color and data-id tag
   fields."
  [{:keys [value id]}]
  [div-grid-col {:style {:border "1px solid #d3d3d3"
                         :background (if ((:selected @app-state) id)
                                       "#999999"
                                       "#aaaaaa")}
                 :data-id id
                 :id (str "grid-" id)
                 :on-click toggle-selected}
   value])

; We could clean this up, but it should work for now.
(defn build-game
  "Reagent component that contains our game grid."
  []
  (let [numbers (:numbers @app-state)]
    [div-grid-container
     (into [div-grid-row] (for [x (range 0 4)]
                            (small-cell (nth numbers x))))
     (into [div-grid-row] (for [x (range 4 8)]
                            (small-cell (nth numbers x))))
     (into [div-grid-row] (for [x (range 8 12)]
                            (small-cell (nth numbers x))))
     (into [div-grid-row] (for [x (range 12 16)]
                            (small-cell (nth numbers x))))
     (into [div-grid-row] (for [x (range 16 20)]
                            (small-cell (nth numbers x))))
     (into [div-grid-row] (for [x (range 20 24)]
                            (small-cell (nth numbers x))))
     ]))

(defn set-numbers-and-target!
  "Set the state of the numbers and the sum target -- call this one"
  []
  (let [num-list (generate-numbers)
        target   (generate-target num-list)]
    (swap! app-state assoc :numbers (wrap-numbers (shuffle num-list)) :target target)))

(defn attach-touch-listeners
  "Add the touch listeners to all of the grid-cell divs in our game. Needs
   to happen after each rendering of the game grid."
  []
  (let [els (get-elements-by-class-name "grid-cell")
        size (.-length els)]
    (doseq [e (range size)]
      (.addEventListener (aget els e) "touchstart" toggle-selected))))

(defn init! []
  (reset! app-state initial-game-state)
  (set-numbers-and-target!)
  (reagent/render-component [my-app] (get-element-by-id "app-name"))
  (reagent/render-component [timer-component] (get-element-by-id "timer"))
  (reagent/render-component [build-game] (get-element-by-id "game"))
  (reagent/render-component [score-component] (get-element-by-id "score"))
  (start-timer!)
  (attach-touch-listeners))

(init!)

(comment
  (-> (generate-numbers)
      (generate-target))
  (set-numbers-and-target!)
  (js/alert "Hello Justin!"))


