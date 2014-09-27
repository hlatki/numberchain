(ns number-chain.core
  (:require [reagent.core :as reagent :refer [atom]]))

(def app-state (atom {:app-name "Number Chain"}))

(defn get-element-by-id [id]
  (.getElementById js/document id))

(defn my-app []
  [:div (str "Hello from: " (:app-name @app-state))])

(defn init! []
  (reagent/render-component [my-app] (get-element-by-id "app-name")))

(init!)

(comment
  (js/alert "Hello Justin!"))
