# Unreleased

* problems/test-codes for production from `py99`
* admin/toggle-status
* effective upsert. update only changed elements
* pu-4 and wu-4 does not take effects.
* reports who uploads now(SSE)
* flag skip-validation
* admin can see his/her answers/comments
* restrict comments to themselves


# 0.3.19 (2025-10-04)

- made a python included container `hkim0331/konpy2:0.3.19`,
  which can be downloaded?
- path of ruff
- copied Dockerfile, Makefile from `docker/hkim0331-clojure`
- container created, but did not show the admin page.

# 0.3.18 (2025-10-04)

- added - dislay paths of python, pytest ruff in the admin page.
- changed: introduced new redis list variables
    - wil2:<user>:comments:<today>
    - wil2:<user>:uploads:<today>
  which never expires,
  and which replaced `wil2:<user>:comments` and `wil2:<user>:uploads`
- display redis vars in the admin page.
- divided admin page into three sections,
    - problems-section
    - env-vars section
    - redis-vars section
- updated library - following datascript update (1.7.5 -> 1.7.6).

| :file    | :name                                        | :current | :latest |
|----------|----------------------------------------------|----------|---------|
| deps.edn | io.github.hkimjp/datascript-storage-javatime | 0.7.4    | 0.7.5   |

# 0.3.17 (2025-10-01)

- improved `util/start-day`
- added env var `START_DAY`
- added `compose.yml`
- added `:jvm-opts  ["--enable-native-access=ALL-UNNAMED"` to alias `run-m`

# 0.3.16 (2025-09-27)

- improved `admin` page

# 0.3.15 (2025-09-27)

- reconsidered `restrictions.clj`

# 0.3.14 (2025-09-26)

- improved `bump-version-local.sh`
- fixed bug in stocks - other one's stocks were browsable
- updated clojure

| :file    | :name               | :current | :latest |
|----------|---------------------|----------|---------|
| deps.edn | org.clojure/clojure | 1.12.2   | 1.12.3  |


# 0.3.13 (2025-09-25)

- peep other student's points
- changed scores.clj - `to:` -> `problem:`
- updated jetty

| :file    | :name                   | :current | :latest |
|----------|-------------------------|----------|---------|
| deps.edn | ring/ring-jetty-adapter | 1.15.2   | 1.15.3  |

# 0.3.12 (2025-09-24)

- `hover:underline` answers/comments in `scores` page
- sort answers/comments in `scores` page
- show updated: on `/k/scores` page
- show to: on `/k/scores` page. recursion is something!


# 0.3.11 (2025-09-23)

# 0.3.10 (2025-09-23)

- improved scores page. links to the answers, comments.

# 0.3.9 (2025-09-22)

- reformat divs. 0.3.8's formation is better?
- display restriction constants in admin page.

# 0.3.8 (2025-09-22)

- improved `bump-version.sh`
- show authors name who sent the same answer.
- added `digest.clj`
- {:class "bg-lime-200 h-40 border-1 p-2 w-2/3"}
- throw exception when not find ruff, python, pytest.
- throw exception when uploading without choosing a file

# 0.3.7 (2025-09-21)

- enabled markdown in comments.
- fixed: "yy-MM-dd HH:mm " is the correct format.
- stopped using `[***]` as a placeholder.
- added login account in the title of the page.
- stops if redis does not respond.
- do not receive empty comments
- font size - stocks/preview text-sm
- taller `your comment` height, make it bg-green-200.
- added nextjournal/markdown to dependencies.

# 0.3.6 (2025-09-20)

- removed "under construction" from `help.clj`
- fixed - display author name for his/her answers.
- can stock and display a list of the first lines of the stocked text.
- link to stocks

# 0.3.5 (2025-09-20)

- A,B,C to comments
- comment points - attr :pt
- **BREAKING** anonymize author names
- no anonymize self

# 0.3.4 (2025-09-20)

- reconsidered t/log! level, facilities
- let's use redis-cli `flushall` in development
- showed commemt to whom?
- fixed: not `e`, should `(.getMessage e)`. error messages are too noisy.
- flag loosen-restriction by .env or konpy.env
- bufix validate/pytest

      (when-not (empty? testcode)
        (t/log! {:level :error :data {:testcode testcode
                                      :empty? (empty? testcode)}})

- sort todays-answers

# 0.3.3 (2025-09-19)

- use `java-time.api/before?` instead of `jt/before?` to avoid the error,

  :Unknown predicate 'jt/before? in [(jt/before? ?now ?updated)])

- resume to work on dq.local (was: not work on eq.local)
- added `tasks/todays-answers` and `tasks/todays-comments`
- added routes
    ["hx-answers"]  {:get tasks/hx-answers}
    ["hx-comments"] {:get "tasks/hx-comments"}


# 0.3.2 (2025-09-18)

- added `util/iso`
- route-handler does not know who is the user.
- changed
    how many comments/uploads can be done in 24hours?
    - MAX_COMMENTS = 2
    - MAX_UPLOADS  = 2
- order comments chronologically
- adjust log levels
- restrictions/before-* check uploads/comments frequencies and counts
- restrictions/after-* reloads the process time and update counter
- changed: use try~catch to simplify codes

# 0.3.1 (2025-09-18)

- improved/clean-upped the admin page

# 0.3.0 (2025-09-18)

- java25 on macos
- restrictions
    - system/min-interval-comments
    - system/min-interval-uploads
    * system/max-comments
    * system/max-uploads
    - system/kp2-flash
- restrict comments
- FIXED cyclic load dependency by removing system things to restrictions namespace.

  Cyclic load dependency: [ /hkimjp/konpy2/system ]->/hkimjp/konpy2/restrictions->/hkimjp/konpy2/comments->/hkimjp/konpy2/routes->[ /hkimjp/konpy2/system ]->/hkimjp/konpy2/main

- FIXED: emulate flash.
- restrict uploads
- development on macos need poetry? without poetry, counld not find pytest.
- added `poetry.lock`
- added `pyproject.toml`
- bump-version-local.sh treats `pyproject.toml`

# 0.2.16 (2025-09-17)

- **BREAKING** attr :test -> :testcode
- fixed bug - validate/get-last-answer
- validate/pytest
- /usr/bin/pytest (/opt/homebrew/bin/pytest)

# 0.2.15 (2025-09-17)

- added doctest
- added ruff - ruff requires '\n' at the end of a code.
- added timeout-shell/timeout-shell {:mvn/version "1.0.0"}
- added babashka/fs {:mvn/version "0.5.27"}
- ruff versions
    - ubuntu/snap 0.12.12
    - macos/brew 0.13.0
    - VScode 0.13.0
- added `validate/get-last-answer`
- added `validate/expand-includes`
- two or more anwers from a student to some problems - must take the last answer.
- log logout! user - OK. logout! can be called without login.
- updated carmine-farm

| :file    | :name                         | :current | :latest |
|----------|-------------------------------|----------|---------|
| deps.edn | io.github.hkimjp/carmine-farm | 0.2.4    | 0.2.6   |
|          | ring/ring-jetty-adapter       | 1.15.1   | 1.15.2  |

# 0.2.14 (2025-09-17)

- app.melt:konpy-service - stop using /etc/default, instead /home/ubuntu/konpy
- made comment area wider w-2/5
- renamed comments/post-comment -> comments/comment!
- renamed comments/show-comment -> comments/hx-comment
- renamed answers/post-answer -> answers/answer!
- renamed answers/show-answer -> answers/hx-answer

# 0.2.13 (2025-09-16)

- learning tailwind
- if found chatgpt's answer, show it next to the author's answer
- removed `charred`, `fast-edn`, `markdown` from dependencies

# 0.2.12 (2025-09-16)

- bugfix: typo in `tasks.clj`. fixed.

# 0.2.11 (2025-09-16)

- added user/problem! and user/problems!
- refactord
- app.melt
- deploy test to app.melt
    - copy konpy.env to `/etc/defaults/konpy.env`
    - **bug** can not flow `/k/problem/1` etc.

# 0.2.10 (2025-09-16)

- can show comments
- too narrow click on numbers?
- hover:underline problem links

# 0.2.9 (2025-09-15)

- **BREAKING**  `get /k/answer/:e/:p`
  e is answer id.
  p is problem id.

    ["answer/:e/:p"   {:get  answers/show-answer}]

- can add comments
- post /k/comment - create comment
- get /k/comment/:e - fetch comment comment id `e`
  fetching comments to answer A is done by a htmx call.
- answers namespace divided from tasks namespace

# 0.2.8 (2025-09-15)

- changed valid => status (true/false => "yes"/"no")
- changed admin/upsert!

# 0.2.7 (2025-09-15)

- updated ring-defaults, ring-jetty-adapter

| :file    | :name                   | :current | :latest |
|----------|-------------------------|----------|---------|
| deps.edn | ring/ring-defaults      | 0.6.0    | 0.7.0   |
|          | ring/ring-jetty-adapter | 1.14.2   | 1.15.1  |

# 0.2.6 (2025-09-15)

- list answers
- show author's answer
- list answerers with htmx
- removed `answers.clj` - merged into `tasks.clj`

# 0.2.5 (2025-09-14)

- upload answers
* tasks/div-answers do not wrap.

# 0.2.4 (2025-09-14)

- checked routes

# 0.2.3 (2025-09-13)

- /tasks
- util/week
- /
- /problem/:e
- added `src/hkimjp/konpy2/answers.clj`
- added `src/hkimjp/konpy2/comments.clj`
- added `src/hkimjp/konpy2/hx.clj`
- added `src/hkimjp/konpy2/tasks.clj`

# 0.2.2 (2025-09-12)

- /admin/problems
- /admin/new
- /admin/update/:e
- unified /admin/new! and /admin/edit! into /admin/upsert!

# 0.2.1 (2025-09-12)

- problem creation
- tailwindcss > /dev/null 2> &1
- get  admin/new => admin/new
- post admin/new => admin/create! => admin/upsert!

# 0.2.0 (2025-09-11)

- renamed `view.clj` to `response.clj`. the namespace provides `page` and `hx`.
- (System/exit 0) if startup fails
- cardinarity many? no. answers/comments has an only one parent.
- `doc/data-structure.md`. should be `schema.md`? renamed.

# 0.1.1 (2025-09-10)

- renamed `core.clj` to `main.clj`
- fixed `bump-version-local.sh`
- navbar, routing

# 0.1.0 (2025-09-10)

- initialized repository
