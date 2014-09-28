(ns number-chain.timer
  (:require [reagent.core :refer [atom]]))


(def count-down (atom 10)) ;; The count-down var used by the timer

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
  [:div "Timer: " (repeat @count-down \I)])

