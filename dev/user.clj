(ns user
  (:require
   ; [babashka.fs :as fs]
   ; [clojure.java.io :as io]
   [clj-reload.core :as reload]
   ; [java-time.api :as jt]
   [taoensso.telemere :as t]
   [hkimjp.carmine :as c]
   [hkimjp.datascript :as ds]
   [hkimjp.konpy2.util :refer [now]]
   [hkimjp.konpy2.system :refer [start-system stop-system] :as sys]))

(t/set-min-level! :debug)

(start-system)

(reload/init
 {:dirs ["src" "dev" "test"]
  :no-reload '#{user}
  :unload-hook 'before-unload
  :after-reload 'start-system})

(defn before-unload []
  (stop-system))

(defn after-reload []
  (start-system))

;; (reload/reload)

;------------------------------------

(comment
  (require '[clojure.string :as str])
  (def line "abc\ndef\nxyz\n")

  (count (str/split-lines line))

  (def line1 "# include 1_1")
  (def line2 "# from kp1_1")
  (or (re-matches #"#\s*from\s*(kp)*(\d+)_(\d+).*" line2)
      (re-matches #"#\s*include\s*(kp)*(\d+)_(\d+).*" line2))
  :rcf)
;------------------------------------

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
  (nil? "1")
  (nil? "")
  (empty? "1")
  (empty? "")
  (ds/pl 146)
  [["y=f(x) ただし f(x)=2*x**2-3 を-2<=x<3の範囲でプロットする関数 "]
   ["f=f(x) ただし f(x)=3sin(2x)を 0<=x<2pi の範囲でプロットする関数。"]
   ["y=sin(x), y=sin(x+pi/3), y=sin(x+2*pi/3),y=sin(x+pi), y=sin(x+4*pi/3), y=sin(x+5*pi/3) の6つを一つのグラフに。(0<= x <=2Pi)"]
   ["y=2x**2-1 と y=x+2 の交点をグラフから求めよ。もとまった交点の座標を関数コメント内に。"]
   ["plot()を利用し円のグラフをかく関数。楕円になっちゃうかもだが。"]
   ["色々な半径、色の円で画面を埋め尽くす関数。"]]
  [["0より大きい乱数整数n個のリストを返す関数。"]
   ["1 で作ったリストから偶数だけのリストを作る関数。"]
   ["1 で作ったリストから 3 の倍数を除いたリストを作る関数。"]
   ["1 で作ったリストから 5 または 7 の倍数を除いたリストを作る関数。"]
   ["1 で作ったリストから 5 の倍数のリスト、7の倍数のリストの二つを返す関数。"]
   ["1 で作ったリスト中に最も多く含まれる整数はいくつか？"]])
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
    assert set(eqn2(3, -6, 6)) == {(1 + 1j), (1 - 1j)}"]
    ["整数 x を整数 y で割った時の整数商を返す div(x, y). y==0 の時のテストをどう書くか?"
     ""]
    ["正の整数nの階乗n!を戻り値とする関数 fa(n)." ""]
    ["引数nは3ですか？ is_three(n)" ""]
    ["引数xは文字列ですか？is_string(x)" "def test_is_string():
    assert is_string('x') is True
    assert is_string(3) is False
    assert is_string([3.14]) is False
                                "]])

  (c/ping)
  :rcf)
