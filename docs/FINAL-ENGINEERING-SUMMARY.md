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

The code and configuration received static/manual consistency review in the authoring environment. Automated Maven and Docker execution could not be run there because Maven and Docker were unavailable. `mvn clean verify` is therefore a mandatory reviewer/CI gate, not represented as already passed. The Docker build itself executes `mvn verify`, preventing an image from being produced when compilation, tests, or Checkstyle fail.

## Key risks and controls

- Collision: database unique constraint plus bounded retry.
- Lost analytics updates: atomic SQL increment inside the redirect transaction.
- Unsafe URL types: only absolute HTTP/HTTPS URLs without embedded credentials.
- Expiration: checked before counting or redirecting; expired links return 410.
- Secrets: environment variables; none committed.
- AI risk: no sensitive inputs, all generated output reviewed, human approval required for high-impact changes.

## Assumptions and limitations

Analytics means total clicks and timestamps only. Anonymous API access is prototype-only. Rate limiting, OAuth2 authorization, phishing/malware screening, per-event analytics, distributed cache, asynchronous counting, and multi-region deployment require explicit production requirements and are not included.

## Engineer sign-off checklist

- [ ] Run `mvn clean verify`
- [ ] Run `docker compose up --build` and smoke-test all three endpoints
- [ ] Run dependency, SAST, secret, and image scans
- [ ] Load-test redirect concurrency and confirm no lost counts
- [ ] Review OpenAPI compatibility and Flyway upgrade path
- [ ] Confirm security, privacy, SLO, RTO/RPO, retention, and ownership requirements
- [ ] Record named human approval before release
