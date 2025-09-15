# Unreleased

* tasks/answers - check having uploaded(necessary in chatGPT's world)
  restrict comments?
* scores.clj
* stocks.clj
* clj-reload
* attribute :chatgpt is unnecessary. if found chatgpt's answer, show it.
* admin/toggle-status
* digest namespace
* time restriction


# 0.2.9 (2025-09-15)

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
