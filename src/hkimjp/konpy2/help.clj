(ns hkimjp.konpy2.help
  (:require
   [hkimjp.konpy2.response :refer [page]]))

(defn help
  [_request]
  (page
   [:div.mx-4
    [:div.text-2xl.font-medium "Help"]
    [:br]
    [:div.font-bold "tasks"]
    [:p.mx-4 "今週の Python 課題を表示する。回答はここから。
         他受講生の回答へのリンクもここ。リンクをたどってコメントする。"]
    [:div.font-bold "scores(under construction)"]
    [:p.mx-4 "自分の KONPY 点数。GPT まみれ回答よりも心からのコメントにウェート置きたい。"]
    [:div.font-bold "stocks(under construction)"]
    [:p.mx-4 "いいね・悪いね自分用ストック。"
     "hkimura にストックされた悪いね回答・コメントは後日、晒されるだろう。"]
    [:div.font-bold "logout"]
    [:p.mx-4 "ログアウト。"]
    [:div.font-bold "HELP"]
    [:p.mx-4 "このページ。"]
    [:div.font-bold "admin"]
    [:p.mx-4 "管理者専用ページ。"]]))
