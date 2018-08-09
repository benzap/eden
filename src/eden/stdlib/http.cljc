(ns eden.stdlib.http
  (:require
   [bidi.bidi :as bidi]
   [bidi.verbose]
   [bidi.ring]
   [ring.util.response :refer [response]]
   [org.httpkit.server :as server]
   [org.httpkit.client :as client]
   [eden.def :refer [set-var!]]))


;; Needs to be extended to support EdenFunction
(extend-protocol bidi/Matched
  clojure.lang.IFn
  (resolve-handler [this m] (bidi/succeed this m))
  (unresolve-handler [this m] (when (= this (:handler m)) "")))


(extend-protocol bidi.ring/Ring
  clojure.lang.IFn
  (request [f req _] (f req)))


(defn with-channel-fn [req f]
  (server/with-channel req channel
    (f req channel)))


(def http
  {:router
   {:match-route bidi/match-route
    :path-for bidi/path-for
    :branch bidi.verbose/branch
    :param bidi.verbose/param
    :leaf bidi.verbose/leaf
    :make-handler bidi.ring/make-handler}

   :server
   {:run-server server/run-server
    :with-channel with-channel-fn
    :response response
    :channel
    {:open? server/open?
     :close server/close
     :websocket? server/websocket?
     :send! server/send!
     :on-receive server/on-receive
     :on-ping server/on-ping
     :on-close server/on-close
     }}
   
   :client
   {:get client/get
    :delete client/delete
    :head client/head
    :post client/post
    :put client/put
    :options client/options
    :patch client/patch
    :propfind client/propfind
    :proppatch client/proppatch
    :lock client/lock
    :unlock client/unlock
    :report client/report
    :acl client/acl
    :copy client/copy
    :move client/move
    :request client/request
    }})


(defn import-stdlib-http
  [eden]
  (-> eden
      (set-var! 'http http)))
