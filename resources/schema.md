# problems

:db/id id
:problem/status "yes"|"no"
:week n
:num n
:problem s
:testcode s
:updated jt


# answers

:db/id id
:answer/status "yes"|"no"
:author
:to problem/id
:answer s
:digest sha1
:same authors
:took minutes
:updated jt


# comments

:db/id id
:comment/status "yes"|"no"
:author user
:to answer/id
:comment s
:pt pt
:updated jt


# stocks

:db/id id
:stock/status "yes"|"no"
:owner user
:stock text
:updated jt
