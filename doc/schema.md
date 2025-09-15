# problems

:db/id id
:problem/valid true|false # delete uses
:week n
:num n
:problem s
:test s
:updated jt


# answers

:db/id id
:answer/valid true|false # delete uses
:to id
:author
:answer s
:digest n or s?
:updated jt


# comments

:db/id id
:comment/valid true|false # delete
:author user
:to id
:comment s
:updated jt


# stocks

:db/id id
:stock/valid # delete uses
