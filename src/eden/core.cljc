(ns eden.core
  (:refer-clojure :exclude [eval read-string])
  (:require
   [clojure.string :as str]
   [eden.state :refer [*default-eden-instance*]]
   [eden.std.display :refer [display-node]]
   [eden.std.ast]
   [eden.def]
   [eden.std.evaluator :refer [read-string read-file]])
  #?(:clj (:import clojure.lang.ExceptionInfo)))


(def new-eden-instance eden.state/new-eden-instance)
(def set-error-handler eden.state/set-error-handler)
(def eden eden.state/eden)
(def reset-instance! eden.state/reset-instance!)


(defn set-var!
  [identifier value]
  (eden.def/set-var! *default-eden-instance* identifier value)
  nil)


(defn get-var
  [identifier]
  (eden.state-machine/get-var
   (-> *default-eden-instance* :*sm deref) identifier))


(defmacro with-eden-instance [instance & body]
  `(binding [*default-eden-instance* ~instance]
    ~@body))


(defn eval-expression-fn [tokens]
  (try
    (eden.std.ast/evaluate-expression (:astm *default-eden-instance*) tokens)
    (catch #?(:clj ExceptionInfo :cljs js/Object) ex
      ((:error-handler *default-eden-instance*) ex))))


(defmacro eval-expression
  [& tokens]
  `(eval-expression-fn (vec (quote ~tokens))))


(defn eval-expression-string
  [s]
  (let [tokens (read-string s)]
    (eval-expression-fn tokens)))


;; (eval-expression 2 + 2 * 4)


(defn parse-fn [tokens]
  (let [stmts (eden.std.ast/parse (:astm *default-eden-instance*) tokens)]
    (str/join "\n" (mapv display-node stmts))))


(defmacro parse [& tokens]
  `(parse-fn (vec (quote ~tokens))))


;; (parse 2 + 2)


(defn eval-fn [tokens]
  (eden.std.ast/evaluate (:astm *default-eden-instance*) tokens))


(defmacro eval [& tokens]
  `(eval-fn (vec (quote ~tokens))))


(defn eval-string [s]
  (let [tokens (read-string s)]
    (eval-fn tokens)))


(defn eval-file [spath]
  (let [tokens (read-file spath)]
    (eval-fn tokens)))
