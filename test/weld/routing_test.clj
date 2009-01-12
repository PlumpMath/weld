(ns weld.routing-test
  (:use clj-unit.core)
  (:require [weld.routing :as routing]))

(def show (fn [req] req))

(def routes
  [['weld.routing-test/index          :index     :get  "/"                  ]
   ['weld.routing-test/show           :show      :get  "/show/:slug"        ]
   ['weld.routing-test/page-not-found :not-found :any  "/:path" {:path ".*"}]])

(routing/defrouting "host" routes)

(deftest "recognize"
  (let [[fn-sym params] (routing/recognize router :get "/show/foo")]
    (assert= 'weld.routing-test/show fn-sym)
    (assert= :shown ((resolve fn-sym) :shown))
    (assert= {:slug "foo"} params)))

(deftest "path-info"
  (let [info   [:get "/show/foo" {:extra "bar"}]
        params {:slug "foo" :extra "bar"}]
    (assert= info (routing/path-info router :show params))
    (assert= info (path-info :show params))))

(deftest "path"
  (let [the-path "/show/foo"
        params   {:slug "foo" :extra "bar"}]
    (assert= the-path (routing/path router :show params))
    (assert= the-path (path :show params))))

(deftest "url-info"
  (let [info   [:get "host/show/foo" {:extra "bar"}]
        params {:slug "foo" :extra "bar"}]
    (assert= info (routing/url-info router :show params))
    (assert= info (url-info :show params))))

(deftest "url"
  (let [the-path "host/show/foo"
        params   {:slug "foo" :extra "bar"}]
    (assert= the-path (routing/url router :show params))
    (assert= the-path (url :show params))))
