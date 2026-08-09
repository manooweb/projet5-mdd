# MDD (Monde Du Dév)

MDD is a responsive social network MVP for developers. It provides authentication, themes and subscriptions, an article feed, comments, and profile management.

## Repository layout

| Directory | Purpose |
|---|---|
| [`front/`](front/README.md) | Angular single-page application |
| `back/` | Spring Boot REST API |

## Target technical baseline

The starter code is being modernized. The versions and tools below are the agreed project target; they do not describe the legacy scaffold currently present in `front/` and `back/`.

| Area | Target |
|---|---|
| Java | Eclipse Temurin 25.0.4 |
| Build tool | Maven 3.9.16 with Maven Wrapper |
| Backend | Spring Boot 4.1.0, Spring Data JPA, Flyway, Spring Security |
| Database | MySQL 9.7.1 LTS |
| API documentation | springdoc OpenAPI 3.x and Swagger UI |
| Frontend | Angular 22, strict TypeScript, standalone components, zoneless change detection, SCSS |
| Frontend UI | PrimeNG, subject to a focused validation spike; Angular Material is the fallback |
| Backend testing | JUnit, Mockito, Spring Boot integration tests, MockMvc, and Testcontainers MySQL |
| Frontend testing | Vitest and focused Cypress end-to-end tests |
| API testing | Bruno with a cookie jar and CSRF support |
| Quality | EditorConfig, formatter/linter, JaCoCo, Vitest coverage, and a manual SonarQube Cloud audit |

## Planned local URLs

These URLs will become available as the technical baseline is implemented.

| Service | URL |
|---|---|
| Angular application | `http://localhost:4200/` |
| REST API | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` |

## Documentation

Detailed frontend setup and the currently available commands are documented in [`front/README.md`](front/README.md). Backend setup, database bootstrap, testing, and API documentation will be added as their respective technical foundations are implemented.

This README is a living document and will be updated with each completed technical milestone.
