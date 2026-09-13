# CI Validation

GitHub Actions run 8 executed `mvn -B clean verify` on Java 17 after the final test configuration was added.

- Unit tests: 4 passed, 0 failures, 0 errors, 0 skipped
- End-to-end integration tests: 2 passed, 0 failures, 0 errors, 0 skipped
- Maven result: `BUILD SUCCESS`

The integration test boots the Spring application with H2 in PostgreSQL compatibility mode and verifies create -> redirect -> analytics, plus structured rejection of an unsafe URL scheme.
