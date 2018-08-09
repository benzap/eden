(ns eden.stdlib.html
  (:require
   [hiccup.core :as hiccup]
   [hickory.core :as hickory]
   [garden.core :refer [css style]]
   [garden.color :as color]
   [garden.units :as units]
   [eden.std.exceptions :refer [not-implemented]]
   [eden.def :refer [set-var!]]))


(def html
  {:parse (fn [s] (hickory/as-hiccup (hickory/parse s)))
   :stringify (fn [x] (hiccup/html x))
   :css
   {:gen-css css
    :gen-style style
    :color
    {:rgb color/rgb
     :rgba color/rgba
     :hsl color/hsl
     :hsla color/hsla
     :rgb? color/rgb?
     :hsl? color/hsl?
     :hsla? color/hsla?
     :color? color/color?
     :hex? color/hex?
     :hex->rgb color/hex->rgb
     :rgb->hex color/rgb->hex
     :rgb->hsl color/rgb->hsl
     :hsl->rgb color/hsl->rgb
     :hsl->hex color/hsl->hex
     :hex->hsl color/hex->hsl
     :as-hex color/as-hex
     :as-rgb color/as-rgb
     :as-hsl color/as-hsl
     :saturate color/saturate
     :desaturate color/desaturate
     :lighten color/lighten
     :darken color/darken
     :invert color/invert
     :mix color/mix
     :complement color/complement}
    :units
    {:cm units/cm
     :mm units/mm
     :in units/in
     :px units/px
     :pt units/pt
     :pc units/pc
     :percent units/percent
     :em units/em
     :ex units/ex
     :ch units/ch
     :rem units/em
     :vw units/vw
     :vh units/vh
     :vmin units/vmin
     :vmax units/vmax
     :deg units/deg
     :grad units/grad
     :rad units/rad
     :turn units/turn
     :s units/s
     :ms units/ms
     :Hz units/Hz
     :kHz units/kHz
     :dpi units/dpi
     :dpcm units/dpcm
     :dppx units/dppx}}})


(defn import-stdlib-html
  [eden]
  (-> eden
      (set-var! 'html html)))
