# problems

:db/id id
:problem/status "yes"|"no"
:week n
:num n
:problem s
:test s
:updated jt


# answers

:db/id id
:answer/status "yes"|"no"
:to id
:author
:answer s
:digest n or s?
:updated jt


# comments

:db/id id
:comment/status "yes"|"no"
:author user
:to id
:comment s
:updated jt


# stocks

:db/id id
:stock/valid # delete uses
