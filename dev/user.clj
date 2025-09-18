(ns user
  (:require
   [babashka.fs :as fs]
   ; [clojure.java.io :as io]
   [clj-reload.core :as reload]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.util :refer [now]]
   [hkimjp.konpy2.system :refer [start-system stop-system] :as sys]))

(t/set-min-level! :debug)

(start-system)

(defn restart-system
  []
  (stop-system)
  (start-system))

;; (restart-system)

(defn before-unload []
  (stop-system))

(defn after-reload []
  (start-system))

(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}
  :unload-hook 'before-unload
  :after-reload 'start-system})

;; (reload/reload)

(defn problem! [w n problem testcode]
  (ds/puts! [{:db/id -1
              :problem/status "yes"
              :week w
              :num n
              :problem problem
              :testcode testcode
              :updated (now)}]))

(defn problems! [m p-t]
  (doseq [[n [p t]] (map-indexed vector p-t)]
    (problem! m n p t)))

(comment
  (problems!
   0
   [["関数 hello(s) を定義しなさい。 引数の s は文字列。hello('Japan') は文字列 'Hello, Japan!' を返す。スペース文字、びっくりマークに注意。"
     "def test_hello( ):
    assert hello('Japan') == 'Hello, Japan!'
    assert hello('Good bye') == 'Hello, Good bye!'"]

    ["数 x, y を引数にとり、 それらを足した数を戻り値とする関数 add2(x, y)."
     "def test_add2( ):
    assert add2(1,2) == 3
    assert add2(-10,-10) == -20
    assert add2(123, 234) == 357
    assert add2(0.0, 0) == 0
    assert -0.0001 < add2(3.14, 2.58) - 5.72 < 0.0001
    assert -0.0001 < add2(1.1, 2.2) - 3.3 < 0.0001"]

    ["数 x, y を引数にとり、 それらを足した数をプリントする関数 add2_(x, y)." ""]

    ["円の半径を引数にとり、その円の面積を戻り値とする関数en(r)."
     "def test_en( ):
    assert -0.01 < en(1) - 3.14 < 0.01
    assert -1.0 < en(10) - 314 < 1.0"]

    ["二次方程式 ax^2 + bx +c = 0 の a,b,c を引数に取り、 方程式の解をリストで返す関数 eqn2(a, b, c). 重解や虚数解にも対応すること。ひとまず a, b, c は実数としよう。"
     "def test_eqn2():
    assert set(eqn2(1,-3,2)) == {1,2}
    assert set(eqn2(1,-2,1)) == {1}
    assert set(eqn2(1,-1,-6)) == {-2, 3}
    assert set(eqn2(1, -6, 13)) == {(3 + 2j), (3 - 2j)}
    assert set(eqn2(3, -6, 6)) == {(1 + 1j), (1 - 1j)}"]])

  :rcf)

(comment
  (let [file (fs/create-temp-file {:suffix ".py"})]
    (spit (fs/file file) "print('use bb?\nhow is it?\n')")
    (println (slurp (fs/file file)))
    (fs/delete-if-exists (fs/file file))
    (fs/exists? (fs/file file)))
  :rcf)

(comment
  sys/min-interval-answers
  sys/min-interval-comments
  sys/min-interval-uploads
  sys/max-comments
  sys/max-uploads
  :rcf)
