(ns eden.utils.context)


(def default-context
  {::name ::default
   ::state {}})


(defn add-variable-scope
  [context]
  (assoc context ::variables {}))


(defn set-name
  [context name]
  (assoc context ::name name))


(defn set-state
  [context state]
  (assoc context ::state name))
