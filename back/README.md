# MDD backend

This directory contains the Spring Boot REST API for MDD. It manages authentication, users, topics and subscriptions, articles, comments, and comment notifications.

## Architecture

The application is a modular monolith organized by domain:

| Package | Responsibility |
|---|---|
| `authentication/` | Registration, login, logout, JWT-cookie authentication, and password validation |
| `user/` | Current user profile retrieval and update |
| `topic/` | Topic listing and subscriptions |
| `post/` | Feed, article creation, and article detail |
| `comment/` | Comment data and post-comment notifications |
| `system/` | Security configuration, API errors, validation, and technical homepage |

Each domain groups its controller, DTOs, services, repositories, and entities where applicable. The API is exposed under `/api` and documented through Swagger UI.

## Prerequisites and configuration

Use Java 25 and Docker Engine with Docker Compose. Create the local configuration file before the first startup:

```bash
cp .env.example .env
```

Set strong local MySQL passwords and a Base64-encoded `MDD_JWT_SECRET` of at least 32 bytes. `.env` is ignored by Git and must not be committed.

The relevant local configuration values are:

| Variable | Purpose |
|---|---|
| `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD` | Local MySQL database and credentials |
| `MDD_JWT_SECRET` | JWT signing secret |
| `MDD_JWT_SECURE_COOKIE` | `false` for local HTTP development only |
| `MDD_JWT_EXPIRATION` | Authentication-cookie lifetime, defaulting to `PT8H` |
| `MDD_MAIL_FROM` | Sender address for comment notifications |

## Run locally

```bash
./mvnw spring-boot:run
```

Spring Boot detects `compose.yaml` and starts MySQL on host port `33306` and Mailpit. The API listens on port `9001`.

| Service | URL |
|---|---|
| Backend homepage | `http://localhost:9001/` |
| Swagger UI | `http://localhost:9001/swagger-ui/index.html` |
| OpenAPI specification | `http://localhost:9001/v3/api-docs` |
| Health | `http://localhost:9001/actuator/health` |
| Database health | `http://localhost:9001/actuator/health/db` |
| Mailpit inbox | `http://localhost:8025/` |

## Data and security

Flyway creates `users`, `topics`, `subscriptions`, `posts`, and `comments`. The second migration creates the local `demo` user; the third migration creates the topic catalog.

Passwords are hashed with BCrypt. A signed JWT is stored in the `HttpOnly` `MDD_AUTH_TOKEN` cookie. The API keeps CSRF protection enabled: the client must first call `GET /api/auth/csrf`, then include the `X-XSRF-TOKEN` header for state-changing requests. Authentication, authorization, request validation, and centralized JSON error responses protect API access.

## Commands

| Purpose | Command |
|---|---|
| Compile and run unit tests | `./mvnw test` |
| Run unit and integration tests, generate coverage, check thresholds and formatting | `./mvnw verify` |
| Check Java formatting | `./mvnw spotless:check` |
| Apply Java formatting | `./mvnw spotless:apply` |
| Generate Javadoc | `./mvnw javadoc:javadoc` |
| Run the SonarQube Cloud scan workflow | `node scripts/run-sonar-scan.mjs` |

Integration tests use an isolated MySQL Testcontainers instance, so Docker must be available. Surefire runs `*Test` classes; Failsafe runs `*IT` classes during `verify`. The SonarQube Cloud workflow requires `SONAR_TOKEN` in the repository-root `.env` file and runs `clean verify` before the analysis.

## Related documentation

- [Repository README](../README.md): full-stack installation, complete API summary, frontend commands, Bruno checks, and coverage reports.
- [Javadoc conventions](../docs/javadoc/DOC_CONVENTIONS.md): conventions used in the Java source.<br>Generated Javadoc starts at [docs/javadoc/apidocs/index.html](../docs/javadoc/apidocs/index.html).
- [Coverage report index](../docs/reports/coverage/index.html): combined Java, Vitest, and Cypress reports.
