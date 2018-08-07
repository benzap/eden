(ns eden.stdlib.specter
  (:require
   [com.rpl.specter :as specter]
   [eden.std.exceptions :refer [not-implemented]]
   [eden.def :refer [set-var!]]))


(def specter
  {
   :AFTER-ELEM specter/AFTER-ELEM
   :ALL specter/ALL
   :ALL-WITH-META specter/ALL-WITH-META
   :ATOM specter/ATOM
   :BEFORE-ELEM specter/BEFORE-ELEM
   :before-index specter/before-index
   :BEGINNING specter/BEGINNING
   :codewalker specter/codewalker
   :collect specter/collect
   :collect-one specter/collect-one
   :collector #(not-implemented "Macro 'collector' without equivalent function")
   :comp-paths specter/comp-paths
   :compact specter/compact
   :cond-path specter/cond-path
   :continue-then-stay specter/continue-then-stay
   :continuous-subseqs specter/continuous-subseqs
   :DISPENSE specter/DISPENSE
   :dynamic-param? specter/dynamic-param?
   :eachnav specter/eachnav
   :END specter/END
   :filterer specter/filterer
   :FIRST specter/FIRST
   :if-path specter/if-path
   :index-nav specter/index-nav
   :INDEXED-VALS specter/INDEXED-VALS
   :indexed-vals specter/indexed-vals
   :keypath specter/keypath
   :LAST specter/LAST
   :late-path specter/late-path
   :late-resolved-fn specter/late-resolved-fn
   :local-declarepath specter/local-declarepath
   :map-key specter/map-key
   :MAP-KEYS specter/MAP-KEYS
   :MAP-VALS specter/MAP-VALS
   :META specter/META
   :multi-path specter/multi-path
   :multi-transform specter/multi-transform*
   :must specter/must
   :NAME specter/NAME
   :NAMESPACE specter/NAMESPACE
   :NIL->LIST specter/NIL->LIST
   :NIL->SET specter/NIL->SET
   :nil->val specter/nil->val
   :NIL->VECTOR specter/NIL->VECTOR
   :NONE specter/NONE
   :NONE-ELEM specter/NONE-ELEM
   :not-selected? specter/not-selected?
   :nthpath specter/nthpath
   :parser specter/parser
   :pred specter/pred
   :pred< specter/pred<
   :pred<= specter/pred<=
   :pred= specter/pred=
   :pred> specter/pred>
   :pred>= specter/pred>=
   :putval specter/putval
   :regex-nav specter/regex-nav
   :replace-in specter/replace-in*
   :select specter/select*
   :select-any specter/select-any*
   :select-first specter/select-first*
   :select-one specter/select-one*
   :select-one! specter/select-one!*
   :selected-any? specter/selected-any?*
   :selected? specter/selected?
   :set-elem specter/set-elem
   :setval specter/setval*
   :srange specter/srange
   :srange-dynamic specter/srange-dynamic
   :STAY specter/STAY
   :stay-then-continue specter/stay-then-continue
   :STOP specter/STOP
   :submap specter/submap
   :subselect specter/subselect
   :subset specter/subset
   :terminal specter/terminal
   :terminal-val specter/terminal-val
   :transform specter/transform*
   :transformed specter/transformed
   :traverse specter/traverse*
   :traverse-all specter/traverse-all*
   :traversed specter/traversed
   :VAL specter/VAL
   :view specter/view
   :vterminal specter/vterminal
   :walker specter/walker
   :with-fresh-collected specter/with-fresh-collected
   :wrap-dynamic-nav specter/wrap-dynamic-nav
   })


(defn import-stdlib-specter
  [eden]
  (-> eden
      (set-var! '$ specter)))
