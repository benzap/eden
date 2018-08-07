(ns eden.stdlib.specter
  (:require
   [com.rpl.specter :as specter]
   [com.rpl.specter.impl :as specter.impl]
   [eden.std.exceptions :refer [not-implemented]]
   [eden.def :refer [set-var!]]))


(def specter
  {:AFTER-ELEM specter/AFTER-ELEM
   :ALL specter/ALL
   :ALL-WITH-META specter/ALL-WITH-META
   :ATOM specter/ATOM
   :BEFORE-ELEM specter/BEFORE-ELEM
   :before-index specter/before-index
   :BEGINNING specter/BEGINNING
   :codewalker specter/codewalker
   :collect specter/collect
   :collect-one specter/collect-one
   :collected? specter.impl/collected?*
   :collector #(not-implemented "Macro 'collector' without equivalent function")
   :comp-paths specter/comp-paths
   :compact specter/compact
   :compiled-multi-transform specter/compiled-multi-transform
   :compiled-replace-in specter/compiled-replace-in
   :compiled-select specter/compiled-select
   :compiled-select-any specter/compiled-select-any
   :compiled-select-first specter/compiled-select-first
   :compiled-select-one specter/compiled-select-one
   :compiled-select-one! specter/compiled-select-one!
   :compiled-selected-any? specter/compiled-selected-any?
   :compiled-setval specter/compiled-setval
   :compiled-transform specter/compiled-transform
   :compiled-traverse specter/compiled-traverse
   :compiled-traverse-all specter/compiled-traverse-all
   :compiled-vtransform specter/compiled-vtransform
   :cond-path specter/cond-path
   :continue-then-stay specter/continue-then-stay
   :continuous-subseqs specter/continuous-subseqs
   :declarepath #(not-implemented "Macro 'declarepath' without equivalent function")
   :defcollector #(not-implemented "Macro 'defcollector' without equivalent function")
   :defdynamicnav #(not-implemented "Macro 'defdynamicnav' without equivalent function")
   :defmacroalias #(not-implemented "Macro 'defmacroalias' without equivalent function")
   :defnav #(not-implemented "Macro 'defnav' without equivalent function")
   :defprotocolpath #(not-implemented "Macro 'defprotocolpath' without equivalent function")
   :defrichnav #(not-implemented "Macro 'defrichnav' without equivalent function")
   :DISPENSE specter/DISPENSE
   :dynamic-param? specter/dynamic-param?
   :dynamicnav #(not-implemented "Macro 'dynamicnav' without equivalent function")
   :eachnav specter/eachnav
   :END specter/END
   :end-fn #(not-implemented "Macro 'end-fn' without equivalent function")
   :extend-protocolpath #(not-implemented "Macro 'extend-protocolpath' without equivalent function")
   :filterer specter/filterer
   :FIRST specter/FIRST
   :if-path specter/if-path
   :index-nav specter/index-nav
   :INDEXED-VALS specter/INDEXED-VALS
   :indexed-vals specter/indexed-vals
   :keypath specter/keypath
   :LAST specter/LAST
   :late-bound-collector #(not-implemented "Macro 'late-bound-collector' without equivalent function")
   :late-bound-nav #(not-implemented "Macro 'late-bound-nav' without equivalent function")
   :late-bound-richnav #(not-implemented "Macro 'late-bound-richnav' without equivalent function")
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
   :nav #(not-implemented "Macro 'nav' without equivalent function")
   :NIL->LIST specter/NIL->LIST
   :NIL->SET specter/NIL->SET
   :nil->val specter/nil->val
   :NIL->VECTOR specter/NIL->VECTOR
   :NONE specter/NONE
   :NONE-ELEM specter/NONE-ELEM
   :not-selected? specter/not-selected?
   :nthpath specter/nthpath
   :parser specter/parser
   :path #(not-implemented "Macro 'path' without equivalent function")
   :pred specter/pred
   :pred< specter/pred<
   :pred<= specter/pred<=
   :pred= specter/pred=
   :pred> specter/pred>
   :pred>= specter/pred>=
   :providepath #(not-implemented "Macro 'providepath' without equivalent function")
   :putval specter/putval
   :recursive-path #(not-implemented "Macro 'recursive-path' without equivalent function")
   :regex-nav specter/regex-nav
   :replace-in specter/replace-in*
   :richnav #(not-implemented "Macro 'richnav' without equivalent function")
   :satisfies-protpath? #(not-implemented "Macro 'satisfies-protpath?' without equivalent function")
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
   :vtransform #(not-implemented "Macro 'vtransform' without equivalent function")
   :walker specter/walker
   :with-fresh-collected specter/with-fresh-collected
   :with-inline-debug #(not-implemented "Macro 'with-inline-debug' without equivalent function")
   :wrap-dynamic-nav specter/wrap-dynamic-nav})


(defn import-stdlib-specter
  [eden]
  (-> eden
      (set-var! '$ specter)))
