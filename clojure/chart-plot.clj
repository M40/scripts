; insert this code block under namespace in gorilla-repl 

(ns pressure
 (:import [java.io File])
  (:require [gorilla-plot.core :as plot]
            [clojure.data.codec.base64 :as b64]
            [gorilla-renderable.core :as render])
  (:use [clojure repl pprint]
        [clojure.string :only [join]]
        [incanter core io stats charts excel datasets]))

(defn chart->byte-array
  [chart & {:keys [plot-size aspect-ratio]
            :or {plot-size 500
                 aspect-ratio 1.618}}]
  (let [width (/ plot-size aspect-ratio)
        ba (java.io.ByteArrayOutputStream.)
        _ (org.jfree.chart.ChartUtilities/writeChartAsPNG ba chart plot-size width)]
    (.toByteArray ba)))
(defrecord ChartView [content opts])
(defn chart-view [content & opts] (ChartView. content opts))
(extend-type ChartView
  render/Renderable
  (render [self]
          (let [bytes (apply chart->byte-array (:content self) (:opts self))]
            {:type :html
             :content (format "<img src=\"data:image/PNG;base64,%1$s\"/>" (String. (b64/encode bytes)))
             :value (pr-str self)})))
