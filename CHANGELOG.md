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
