# MDD (Monde Du Dév)

MDD is a responsive social-network MVP for developers. Its target functional scope is user authentication, topics and subscriptions, an article feed, articles, comments, and profile management.

## Current implementation status

The technical foundation is in place. The backend currently provides the technical entry point, health and OpenAPI endpoints, as well as the authentication vertical: CSRF initialization, registration, login, and logout. The remaining MVP verticals will be added incrementally with their tests and documentation.

## Project structure

| Directory | Purpose |
|---|---|
| [`front/`](front/README.md) | Angular single-page application |
| `back/` | Spring Boot REST API, database migrations, and backend tests |
| [`bruno/`](bruno/) | Local API collection for Bruno |
| [`docs/reports/coverage/`](docs/reports/coverage/README.md) | Generated Java, Angular, and end-to-end coverage reports |

## Technical stack

| Area | Technology |
|---|---|
| Frontend | Angular 22, TypeScript strict mode, standalone components, zoneless change detection, SCSS |
| Backend | Java 25.0.4 Temurin, Spring Boot 4.1.0, Spring Data JPA, Spring Security, Flyway, Thymeleaf |
| Database | MySQL 9.7.1 |
| API documentation | springdoc OpenAPI 3.0.3 and Swagger UI |
| Authentication | JWT in an `HttpOnly` cookie with CSRF protection |
| Backend tests | JUnit, Mockito, MockMvc, Spring Boot integration tests, and Testcontainers MySQL |
| Frontend tests | Vitest and Cypress |
| API checks | Bruno with a cookie jar and CSRF support |
| Quality | EditorConfig, Spotless, JaCoCo, Vitest coverage, and a manual SonarQube Cloud audit |

## Prerequisites

- Java 25.0.4 (Temurin)
- Node.js 22.22.3 or a later 22.x release
- Docker Engine with Docker Compose, for MySQL and Testcontainers

## Configuration

The backend reads local configuration from `back/.env`. Create it from the committed template before the first local startup:

```bash
cd back
cp .env.example .env
```

Set strong local values for the MySQL passwords and `MDD_JWT_SECRET`. The `.env` file is ignored by Git and must never be committed. `MDD_JWT_SECURE_COOKIE=false` is intended for local HTTP development only; use a secure cookie outside local development.

## Run locally

Start the backend from `back/`:

```bash
./mvnw spring-boot:run
```

Spring Boot Docker Compose support detects `compose.yaml` and manages the local database service for this development run.

In a second terminal, start the frontend:

```bash
cd front
npm install
npm start
```

The Angular development server proxies `/api` requests to the Spring Boot backend, so frontend requests remain same-origin during local development.

## Local services

| Service | URL |
|---|---|
| Angular application | `http://localhost:4400/` |
| Backend technical homepage | `http://localhost:9001/` |
| REST API base path | `http://localhost:9001/api` |
| Swagger UI | `http://localhost:9001/swagger-ui/index.html` |
| OpenAPI specification | `http://localhost:9001/v3/api-docs` |
| Backend health status | `http://localhost:9001/actuator/health` |
| MySQL host port | `localhost:33306` |
| Mailpit | `http://localhost:8025/` |

The public backend homepage is a technical entry point for local development. It links to Swagger UI and shows the current backend service status.

## Architecture and data

The application is a modular Spring Boot monolith exposing a JSON REST API under `/api`, with an Angular SPA in the same repository. The backend is organized by domain; the current `authentication` domain contains its controller, DTOs, services, repository, configuration, and `UserAccount` entity. Cross-cutting technical code is placed in the `system` package.

Flyway creates the current relational schema. It contains `users`, `topics`, `subscriptions`, `posts`, and `comments`; foreign keys model subscriptions, post authors/topics, and comment authors/posts. The topic catalog is seeded by the second migration. The schema is the foundation for the MVP, but only the authentication API is currently exposed.

## Security

Authentication uses a signed JWT stored in the `MDD_AUTH_TOKEN` cookie. The cookie is `HttpOnly`; application code cannot read it. Passwords are hashed with BCrypt. CSRF protection remains enabled for requests that modify data: the client first initializes the CSRF token, then sends the `X-XSRF-TOKEN` header while the readable `XSRF-TOKEN` cookie is present.

`SameSite` limits cross-site cookie delivery, but it does not replace CSRF validation. No credentials, database passwords, or JWT secret belong in versioned files.

## Available API endpoints

Swagger UI is the authoritative interactive API reference. The endpoints currently implemented are listed below.

| Method | Path | Purpose | Successful response |
|---|---|---|---|
| `GET` | `/api/auth/csrf` | Initializes CSRF protection and the `XSRF-TOKEN` cookie | `204 No Content` |
| `POST` | `/api/auth/register` | Creates a user account and starts an authenticated session | `201 Created` |
| `POST` | `/api/auth/login` | Authenticates with a username or email and starts a session | `204 No Content` |
| `POST` | `/api/auth/logout` | Clears the current authentication cookie | `204 No Content` |
| `GET` | `/actuator/health` | Returns the backend health status | `200 OK` |
| `GET` | `/v3/api-docs` | Returns the generated OpenAPI specification | `200 OK` |

For `register`, send `username`, `email`, and `password` in JSON. The password must contain 8 to 72 characters. For `login`, send `login` (username or email) and `password`. Call `GET /api/auth/csrf` before every state-changing authentication request.

## Tests and quality checks

Run backend checks from `back/`:

| Purpose | Command |
|---|---|
| Fast test suite | `./mvnw test` |
| Unit and integration tests, coverage reports, coverage thresholds, and formatting verification | `./mvnw verify` |
| Apply Java formatting | `./mvnw spotless:apply` |

`verify` runs `*Test` classes with Surefire and `*IT` classes with Failsafe. Integration tests use an isolated MySQL Testcontainers instance, so Docker must be available.

Run frontend checks from `front/`:

| Purpose | Command |
|---|---|
| Unit test suite | `npm test` |
| Vitest unit and integration coverage | `npm run test:coverage` |
| Lint | `npm run lint` |
| Check formatting | `npm run format:check` |
| Apply formatting | `npm run format` |

Coverage report entry points are available in [`docs/reports/coverage/index.html`](docs/reports/coverage/index.html). Regeneration commands and the scope of each report are documented in [`docs/reports/coverage/README.md`](docs/reports/coverage/README.md).

## Additional documentation

- [`front/README.md`](front/README.md): frontend-specific setup and commands.
- [`docs/reports/coverage/README.md`](docs/reports/coverage/README.md): generated coverage reports.
- Swagger UI: endpoint details and request/response examples for the running backend.

This README is updated as each MVP vertical is completed. It documents only configuration, commands, endpoints, and results that are present in the repository.
