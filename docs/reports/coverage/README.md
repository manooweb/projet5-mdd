# Coverage reports

This directory contains generated coverage reports intentionally tracked in Git.

Open the [coverage report index](index.html) to access the combined Java, Vitest, and Cypress reports.

| Report                      | Scope                                                                                     | Regeneration command                |
| --------------------------- | ----------------------------------------------------------------------------------------- | ----------------------------------- |
| `back-jacoco/`              | Combined Java unit and integration test coverage, including the integration test ratio.   | `cd back && ./mvnw clean verify`    |
| `back-jacoco-unit/`         | Java unit test coverage.                                                                  | `cd back && ./mvnw clean verify`    |
| `back-jacoco-integration/`  | Java integration test coverage.                                                           | `cd back && ./mvnw clean verify`    |
| `front-e2e/`                | Cypress end-to-end test coverage.                                                         | `cd front && npm run e2e:coverage`  |
| `front-vitest/`             | Combined Vitest unit and integration test coverage, including the integration test ratio. | `cd front && npm run test:coverage` |
| `front-vitest-unit/`        | Vitest unit test coverage.                                                                | `cd front && npm run test:coverage` |
| `front-vitest-integration/` | Vitest integration test coverage.                                                         | `cd front && npm run test:coverage` |

The combined report links to the detailed reports. Each detailed report links back to the combined report.
