(ns weld.http-utils-test
  (:use clj-unit.core
        weld.http-utils))

(deftest "url-escape, url-unescape: round-trip as expected"
  (let [given "foo123!@#$%^&*(){}[]<>?/"]
    (assert= given (url-unescape (url-escape given)))))

(deftest "base64-encode, base64-decode"
  (let [data    {:foo "bar" }
        encoded (base64-encode (pr-str data))]
    (assert-match #"[a-zA-Z0-9\+\/]" encoded)
    (assert= data (read-string (base64-decode encoded)))))

; Test cases adapted from Merb.
(def query-parse-cases
  [[""                                    {}]
   ["foo=bar&baz=bat"                     {:foo "bar", :baz "bat"}]
   ["foo=bar&foo=baz"                     {:foo "baz"}]
   ["foo[]=bar&foo[]=baz"                 {:foo ["bar" "baz"]}]
   ["foo[][bar]=1&foo[][bar]=2"           {:foo [{:bar "1"} {:bar "2"}]}]
   ["foo[bar][][baz]=1&foo[bar][][baz]=2" {:foo {:bar [{:baz "1"} {:baz "2"}]}}]
   ["foo[1]=bar&foo[2]=baz"               {:foo {:1 "bar" :2 "baz"}}]
   ["foo[bar][baz]=1&foo[bar][zot]=2&foo[bar][zip]=3&foo[bar][buz]=4" {:foo {:bar {:baz "1" :zot "2" :zip "3" :buz "4"}}}]
   ["foo[bar][][baz]=1&foo[bar][][zot]=2&foo[bar][][zip]=3&foo[bar][][buz]=4" {:foo {:bar [{:baz "1" :zot "2" :zip "3" :buz "4"}]}}]
   ["foo[bar][][baz]=1&foo[bar][][zot]=2&foo[bar][][baz]=3&foo[bar][][zot]=4" {:foo {:bar [{:baz "1" :zot "2"} {:baz "3" :zot "4"}]}}]])

(doseq [[query-string query-params] query-parse-cases]
  (deftest (format "query-parse: works on %s" query-string)
    (assert= query-params (query-parse query-string))))

(doseq [query-params (map second query-parse-cases)]
  (deftest (format "query-unparse: works on %s" query-params)
    (assert= query-params (query-parse (query-unparse query-params)))))

