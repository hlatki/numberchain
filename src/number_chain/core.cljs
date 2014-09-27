(ns number-chain.core
  (:require [reagent.core :as reagent :refer [atom]]
            [number-chain.numbers :refer [generate-numbers generate-target wrap-numbers]]))

(declare init!)
;; For now, this is our initial game state. On init! we will reset our app-state atom back to this.
(def initial-game-state {:app-name "Number Chain"
                         :numbers {}
                         :target nil
                         :selected #{}})

(def app-state (atom initial-game-state))

(def count-down (atom 10)) ;; The count-down var used by the timer

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

;; Timer related code
;; I tried moving this out to timer.cljs, but could not get the reagent
;; component for the timer to update when it did not live in this ns. This code
;; also breaks the code reloading of perfection. If you are not working with
;; things that require the timer, comment the (start-timer!) call in in init!
;; out to prevent related issues.

(defn timer-fn
  "Function called every time the counter decrements. If the count-down hits
   zero, stop the interval and call the time-up-fn in timer-state."
  []
  (js/setInterval
    (fn [] (do (swap! count-down dec)
               (when (<= @count-down 0)
                 (stop-timer!)
                 ((:time-up-fn @timer-state)))))
    1000))

(def timer-state (atom {:timer-fn timer-fn
                        :timeout nil
                        :time-up-fn (fn [] nil)}))

(defn stop-timer! []
  "Stop the current timers js interval."
  (when-let [timeout (:timeout @timer-state)]
    (js/clearTimeout timeout)))

(defn pause-timer! []
  (stop-timer!)
  (swap! timer-state assoc :timeout nil))

(defn start-timer!
  "Kick the timer off, clearing out any existing setIntervals and
   resetting the countdown back to 10."
  []
  (when-let [timeout (:timeout @timer-state)]
    (js/clearInterval timeout))
  (reset! count-down 10)
  (swap! timer-state assoc :timeout ((:timer-fn @timer-state))))

(defn timer-component []
  [:div "Timer: " @count-down])

(swap! timer-state assoc :time-up-fn (fn [] (js/alert "You lost!") (init!)))

;; End timer code

(defn init! []
  (reset! app-state initial-game-state)
  (set-numbers-and-target!)
  (reagent/render-component [my-app] (get-element-by-id "app-name"))
  (reagent/render-component [timer-component] (get-element-by-id "timer"))
  (reagent/render-component [build-game] (get-element-by-id "game"))
  (start-timer!)
  (attach-touch-listeners))

(init!)

(comment
  (-> (generate-numbers)
      (generate-target))
  (set-numbers-and-target!)
  (js/alert "Hello Justin!"))


