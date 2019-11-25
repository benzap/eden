(ns eden.stdlib.thread
  (:require
   [eden.def :refer [set-var!]]))


(defn future-wrap [f & args]
  (future (apply f args)))


(defn delay-wrap [f & args]
  (delay (apply f args)))


(def thread
  {:future future-wrap
   :delay delay-wrap
   :delay? delay?
   :realized? realized?
   :id #(.getId (Thread/currentThread))
   :name #(.getName (Thread/currentThread))
   :state #(.getState (Thread/currentThread))
   :current #(Thread/currentThread)
   :sleep #(Thread/sleep %)})


(defn import-stdlib-thread
  [eden]
  (-> eden
      (set-var! 'thread thread)))
