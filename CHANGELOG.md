# Unreleased

* clj-reload
* problems/test-codes for production
* scores.clj
* stocks.clj
* admin/toggle-status
* time restrictions
* digest namespace. used only from answers
* `include` function
* effective upsert
* emulate flash
* test-codes as answers
* show chatgtp user's answers

# 0.2.15-SNAPSHOT


- ring/ring-jetty-adapter {:mvn/version "1.15.2"}
- log logout! user - OK. logout! can be called without login.
* validation - ruff, doctest, pytest
- updated carmine-farm

| :file    | :name                         | :current | :latest |
|----------|-------------------------------|----------|---------|
| deps.edn | io.github.hkimjp/carmine-farm | 0.2.4    | 0.2.6   |


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
