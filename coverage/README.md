# Coverage reports

This directory contains generated coverage reports intentionally tracked in Git.

| Report | Scope | Regeneration command |
|---|---|---|
| `back-jacoco/` | Combined Java unit and integration test coverage, including the integration test ratio. | `cd back && ./mvnw clean verify` |
| `back-jacoco-unit/` | Java unit test coverage. | `cd back && ./mvnw clean verify` |
| `back-jacoco-integration/` | Java integration test coverage. | `cd back && ./mvnw clean verify` |
| `front-e2e/` | Cypress end-to-end test coverage. | `cd front && npm run e2e:coverage` |

The combined report links to the detailed reports. Each detailed report links back to the combined report.
