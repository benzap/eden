(ns eden.stdlib.async
  (:require
   [eden.def :refer [set-var!]]
   [clojure.core.async :as async]))


(def async-ns
  {:<!! async/<!!
   :>!! async/>!!
   :admix async/admix
   :alts!! async/alts!!
   :buffer async/buffer
   :chan async/chan
   :close! async/close!
   :dropping-buffer async/dropping-buffer
   :into async/into
   :map async/map
   :merge async/merge
   :mix async/mix
   :mult async/mult
   :offer! async/offer!
   :onto-chan async/onto-chan
   :pipe async/pipe
   :pipeline async/pipeline
   :pipeline-blocking async/pipeline-blocking
   :pipeline-async async/pipeline-async
   :poll! async/poll!
   :promise-chan async/promise-chan
   :pub async/pub
   :put! async/put!
   :put!! async/>!!
   :reduce async/reduce
   :sliding-buffer async/sliding-buffer
   :solo-mode async/solo-mode
   :split async/split
   :sub async/sub
   :take async/take
   :take! async/take!
   :take!! async/<!!
   :tap async/tap
   :timeout async/timeout
   :to-chan async/to-chan
   :toggle async/toggle
   :transduce async/transduce
   :unblocking-buffer? async/unblocking-buffer?
   :unmix async/unmix
   :unmix-all async/unmix-all
   :unsub async/unsub
   :unsub-all async/unsub-all
   :untap async/untap
   :untap-all async/untap-all})


(defn import-stdlib-async
  [eden]
  (-> eden
      (set-var! 'async async-ns)))
