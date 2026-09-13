# AI-Assisted Engineering Execution Log

## Ownership model

AI assisted within bounded engineering tasks. The engineer supplied intent, constraints, acceptance criteria, and technical context; reviewed each output; and retained responsibility for correctness, maintainability, security, and release readiness. No autonomous production action or high-impact approval was delegated to AI.

No credentials, proprietary source code, customer data, production logs, or internal URLs were provided to the AI assistant.

## Task records and traceability

| Task | Intent, constraints, and acceptance criteria supplied to AI | Candidate output | Engineer disposition | Validation evidence |
|---|---|---|---|---|
| Requirement normalization | Build a reviewable URL-shortener prototype in 2-3 days; expose ambiguity; do not invent privacy-sensitive analytics | Core API list, analytics options, reliability risks | Edited - limited analytics to total clicks and timestamps; documented exclusions and assumptions | README assumptions and OpenAPI contract |
| Architecture | Java 17, Spring Boot 3, runnable by a reviewer, production-minded but assessment-sized | Microservices and modular-monolith alternatives | Accepted modular monolith; rejected microservices because operational overhead was unjustified | Architecture and control flow in README |
| Persistence and codes | Durable mappings, non-enumerable codes, collision correctness under concurrency | Random Base62 generator with existence pre-check | Edited - removed race-prone pre-check as the correctness boundary; used a database unique constraint and bounded retry | Flyway constraint and service tests |
| Redirect analytics | Redirect quickly and never lose increments during concurrent access | Load entity, increment field, save entity | Rejected - vulnerable to lost updates; replaced with one atomic SQL increment | Repository update query and end-to-end analytics assertion |
| Expiration | Optional 1-3650 day lifetime; expired links must not redirect or increment | Timestamp check in redirect flow | Accepted after injecting `Clock` for deterministic testing | Expiration unit test; `410 Gone` contract |
| URL security | Accept usable web destinations without turning validation into server-side fetching | Generic URI parsing | Edited - allow only absolute HTTP/HTTPS URLs and reject embedded credentials, relative URLs, `file:`, and `javascript:` | Unit and HTTP validation tests |
| Error contract | Stable client behavior for invalid, unknown, expired, and exhausted-collision cases | Framework-default exceptions | Edited - mapped failures to structured problem responses and deliberate HTTP statuses | Controller integration tests and OpenAPI |
| Test generation | Cover business edge cases and create -> redirect -> analytics | Initial unit and web-test cases | Edited for deterministic time and observable database effects; configured Failsafe so `*IT` tests execute during `verify` | 4 unit tests and 2 integration tests in CI |
| Build debugging | Resolve compilation/test-discovery failures without weakening checks | Candidate source and Maven changes | Accepted only after compilation and full `clean verify`; integration tests were not renamed or skipped | GitHub Actions runs 8 and 12 |
| Containerization | One-command reviewer startup, reproducible build, non-root runtime | Multi-stage Docker build and Compose stack | Edited - build stage executes `mvn verify`; runtime uses an unprivileged user; DB readiness gates app startup | Docker build and Windows 11 local validation |
| Review preparation | Make decisions, risks, limitations, and AI use defensible | Documentation draft | Edited against implemented code and observed test evidence | README, final summary, CI and local validation records |

## Iterative refinement examples

### Collision handling

1. Candidate: check whether a code exists, then insert it.
2. Review finding: two concurrent requests can both pass the check.
3. Revision: make the database unique constraint authoritative and retry failed inserts in separate transactions.
4. Acceptance: bounded to five attempts; exhaustion maps to `503 Service Unavailable`.

### Analytics correctness

1. Candidate: read `clickCount`, increment it in Java, and save.
2. Review finding: concurrent redirects can overwrite increments.
3. Revision: execute `click_count = click_count + 1` atomically in PostgreSQL.
4. Acceptance: redirect integration test observes the increment; a pre-production concurrency load test remains documented.

### Ambiguous analytics requirement

1. Questions: total or unique clicks, event history, referrer, location, identity, retention, and authorization?
2. Decision: implement total count, creation time, last-access time, and expiration only.
3. Rationale: delivers useful aggregate analytics without inventing surveillance or privacy requirements.
4. Follow-up: event-level analytics requires product/legal approval, retention rules, access control, and asynchronous architecture.

## Quality gates applied

| Gate | Status | Evidence or disposition |
|---|---|---|
| Compilation | PASS | Maven `clean verify` on Java 17 |
| Unit tests | PASS | 4 passed; zero failures, errors, or skips |
| End-to-end integration tests | PASS | 2 passed; create, redirect, analytics, and unsafe-scheme behavior |
| API/schema review | PASS for prototype | OpenAPI contract and additive Flyway V1 migration reviewed |
| Runtime smoke test | PASS for confirmed checks | Docker Compose startup, database readiness, health, and URL creation recorded locally |
| Security review | PASS for prototype scope | Scheme/credential validation, no server-side destination fetch, non-root image, no committed secrets |
| Concurrency design review | PASS at code level | Atomic SQL increment and database-enforced uniqueness |
| Dependency/SAST/image scan | Pre-production gate | Must run in the target organization's approved scanning platform |
| Load/concurrency test | Pre-production gate | Requires agreed traffic, latency, and error-rate targets |

## Human approval boundaries

Named human approval is required before production changes to authentication/authorization, schema compatibility, privacy or retention, abuse controls, SLO/RTO/RPO, infrastructure, data migration, or security exceptions. AI output remains advisory within those decisions.
