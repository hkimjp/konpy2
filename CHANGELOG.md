# Unreleased

* tasks
* scores
* stocks
* delete toggle :problem/avail true/false, htmx.
* htmx from problems page
* list answers
* add comments

# 0.2.6-SNAPSHOT (2025-09-15)

- show answer(s)


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
