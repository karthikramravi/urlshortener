# Final Engineering Summary

## Outcome

The submission implements a runnable URL shortener with create, redirect, aggregate analytics, optional expiration, PostgreSQL persistence, schema migrations, health probes, structured errors, tests, and containerized setup. It also records greenfield, brownfield, and ambiguous-requirement execution.

## Rationale

A modular Spring Boot service is the smallest architecture that is operationally credible for this assessment. PostgreSQL gives durable state and a uniqueness invariant. Secure random Base62 codes avoid predictable enumeration. Atomic database increments preserve click counts under concurrency. Minimal analytics avoids collecting unrequested personal data.

## Artifacts

- Production code and configuration
- OpenAPI 3 contract and Flyway migration
- Unit and end-to-end integration tests
- Dockerfile and Docker Compose setup
- Architecture, decomposition, scenario evidence, AI traceability, quality gates, risks, assumptions, and limitations in the README

## Validation status

GitHub Actions runs 8 and 12 executed `mvn -B clean verify` on Java 17 with `BUILD SUCCESS`: four unit tests and two Spring Boot end-to-end integration tests passed with zero failures, errors, or skips. The Docker build also executes `mvn verify`, preventing an image from being produced when compilation or tests fail.

Docker Compose was additionally validated on Windows 11 with Docker Desktop and WSL 2. PostgreSQL became healthy, Spring Boot logged `Started UrlShortenerApplication`, Actuator returned `{"status":"UP"}`, and both ordinary and long URLs produced seven-character short codes. See [LOCAL-VALIDATION.md](LOCAL-VALIDATION.md).

## Key risks and controls

- Collision: database unique constraint plus bounded retry.
- Lost analytics updates: atomic SQL increment inside the redirect transaction.
- Unsafe URL types: only absolute HTTP/HTTPS URLs without embedded credentials.
- Expiration: checked before counting or redirecting; expired links return 410.
- Secrets: environment variables; none committed.
- AI risk: no sensitive inputs, all generated output reviewed, human approval required for high-impact changes.

## Assumptions and limitations

Analytics means total clicks and timestamps only. Anonymous API access is prototype-only. Rate limiting, OAuth2 authorization, phishing/malware screening, per-event analytics, distributed cache, asynchronous counting, and multi-region deployment require explicit production requirements and are not included.

## Assessment completion and production follow-ups

- [x] Run `mvn clean verify`
- [x] Start the complete application and PostgreSQL stack with `docker compose up --build`
- [x] Validate create, redirect, analytics, unsafe URL, expiration, and missing-code behavior through automated tests
- [x] Smoke-test application startup, health, and URL creation against the local Compose stack
- [x] Review the prototype OpenAPI contract and Flyway migration
- [ ] Before production: run organization-approved dependency, SAST, secret, and image scans
- [ ] Before production: load-test redirect concurrency against agreed latency/error targets
- [ ] Before production: approve security, privacy, SLO, RTO/RPO, retention, and ownership requirements
- [ ] Before release: record named human approval for high-impact decisions
