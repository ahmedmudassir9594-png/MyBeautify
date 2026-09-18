---
name: Backend runtime
description: Environment constraints and durable runtime choices for the cosmetics backend.
---

Managed API artifact workflows execute with the artifact directory as their working directory, so commands should use paths relative to that directory.

**Why:** An initial absolute workspace-relative `cd` failed because the managed workflow had already entered the artifact directory.

**How to apply:** Use `cd spring-app && mvn ...` in the artifact workflow and `spring-app/...` for production jar paths. The current managed Java toolchain reports Java 19, so the build target is kept compatible while deployment documentation names Java 21 as the target.