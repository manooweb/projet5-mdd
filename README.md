# MDD (Monde Du Dév)

MDD is a responsive social-network MVP for developers. Users can create an account, subscribe to topics, consult their feed, publish articles, comment on articles, and manage their profile.

The project contains an Angular single-page application and a Spring Boot REST API. The functional interface is in French; repository documentation and source-code documentation are in English.

## Implemented features

- Account registration, login, logout, and session restoration.
- Topic listing, subscription, and unsubscription.
- Feed of articles from subscribed topics, with chronological sorting.
- Article creation, article detail, and comment creation.
- Profile display and update, including an optional password update.
- Email notification to an article author after a comment is committed.
- Responsive desktop and mobile navigation.

## Project structure

| Directory | Purpose |
|---|---|
| [`front/`](front/README.md) | Angular single-page application, frontend tests, and Cypress scenarios |
| [`back/`](back/README.md) | Spring Boot REST API, Flyway migrations, and backend tests |
| [`bruno/`](bruno/) | API collection and local environments for Bruno |
| [`docs/javadoc/`](docs/javadoc/DOC_CONVENTIONS.md) | Java documentation conventions and generated API documentation |
| [`docs/reports/coverage/`](docs/reports/coverage/README.md) | Generated Java, Angular, and end-to-end coverage reports |

## Technical stack

| Area | Technology |
|---|---|
| Frontend | Angular 22, TypeScript strict mode, standalone components, zoneless change detection, SCSS, PrimeNG, and Tailwind CSS |
| Backend | Java 25, Spring Boot 4.1, Spring Data JPA, Spring Security, Flyway, Thymeleaf, and Spring Mail |
| Database | MySQL 9.7 |
| API documentation | springdoc OpenAPI 3 and Swagger UI |
| Authentication | JWT in an `HttpOnly` cookie with CSRF protection |
| Backend tests | JUnit, Mockito, MockMvc, Spring Boot integration tests, and Testcontainers MySQL |
| Frontend tests | Vitest and Cypress |
| API checks | Bruno with a cookie jar and CSRF support |
| Quality | EditorConfig, Spotless, JaCoCo, ESLint, Prettier, Vitest coverage, Cypress coverage, and a manual SonarQube Cloud audit |

## Prerequisites

- Java 25, preferably Temurin 25.0.4.
- Node.js 22.22.3 or a later 22.x release.
- Docker Engine with Docker Compose. It is required for the local MySQL and Mailpit services, Testcontainers, and the isolated Bruno checks.

## Configuration

The backend reads local configuration from `back/.env`. Create it from the committed template before the first local startup:

```bash
cd back
cp .env.example .env
```

Set strong local values for the MySQL passwords and `MDD_JWT_SECRET`. The `.env` file is ignored by Git and must never be committed.

`MDD_JWT_SECURE_COOKIE=false` is only for local HTTP development. A deployed application must use HTTPS and a secure authentication cookie. `MDD_MAIL_FROM` configures the sender address used for comment notifications.

## Run locally

Start the backend from `back/`:

```bash
./mvnw spring-boot:run
```

Spring Boot Docker Compose support detects `compose.yaml` and starts MySQL and Mailpit for this development run.

In a second terminal, start the frontend:

```bash
cd front
npm install
npm start
```

The Angular development server proxies `/api` requests to Spring Boot. The browser therefore uses the same origin during local development.

## Local services

| Service | URL |
|---|---|
| Angular application | `http://localhost:4400/` |
| Backend technical homepage | `http://localhost:9001/` |
| REST API base path | `http://localhost:9001/api` |
| Swagger UI | `http://localhost:9001/swagger-ui/index.html` |
| OpenAPI specification | `http://localhost:9001/v3/api-docs` |
| Backend health status | `http://localhost:9001/actuator/health` |
| Database health status | `http://localhost:9001/actuator/health/db` |
| Mailpit inbox | `http://localhost:8025/` |
| MySQL host port | `localhost:33306` |

The public backend homepage is a technical entry point for local development. It links to Swagger UI, Mailpit, and backend health information.

## Architecture and data

The application is a modular Spring Boot monolith exposing a JSON REST API under `/api`, with an Angular SPA in the same repository. The frontend is organized by feature (`auth`, `topic`, `post`, and `user`) and by shared UI concerns. Typed HTTP services manage API calls; signals are used for synchronous local state, and RxJS is used for HTTP flows.

The backend is organized by domain: `authentication`, `topic`, `post`, `comment`, and `user`. Each domain keeps its controller, DTOs, service, repository, and, where needed, JPA entities together. Cross-cutting configuration, error handling, security, and validation code lives in `system`.

Flyway creates the relational schema. It contains `users`, `topics`, `subscriptions`, `posts`, and `comments`. Foreign keys model user subscriptions, article authors and topics, and comment authors and articles. The second migration creates the local demo user and the third migration seeds the topic catalog.

## Security

Authentication uses a signed JWT stored in the `MDD_AUTH_TOKEN` cookie. The cookie is `HttpOnly`, so application code cannot read it. Passwords are hashed with BCrypt.

CSRF protection remains enabled for requests that modify data. The client first initializes the readable `XSRF-TOKEN` cookie through `GET /api/auth/csrf`, then sends its value in the `X-XSRF-TOKEN` header. `SameSite` limits cross-site cookie delivery but does not replace CSRF validation.

Protected API routes require a valid session. Validation, centralized JSON error responses, and ownership checks protect data access. No credentials, database passwords, or JWT secret belong in versioned files.

## API

Swagger UI is the authoritative interactive API reference, including schemas and request/response examples. The endpoints below summarize the current API.

| Method | Path | Purpose |
|---|---|---|
| `GET` | `/api/auth/csrf` | Initializes the CSRF cookie |
| `POST` | `/api/auth/register` | Creates an account and authenticated session |
| `POST` | `/api/auth/login` | Opens an authenticated session |
| `POST` | `/api/auth/logout` | Clears the authentication cookie |
| `GET` | `/api/users/me` | Returns the current user profile |
| `PATCH` | `/api/users/me` | Updates the current user profile |
| `GET` | `/api/topics` | Lists topics with the current subscription status |
| `POST` | `/api/topics/{topicId}/subscription` | Subscribes the current user to a topic |
| `DELETE` | `/api/topics/{topicId}/subscription` | Unsubscribes the current user from a topic |
| `GET` | `/api/posts?sort=asc|desc` | Lists articles from subscribed topics |
| `POST` | `/api/posts` | Creates an article |
| `GET` | `/api/posts/{postId}` | Returns an article and its comments |
| `POST` | `/api/posts/{postId}/comments` | Creates a comment |

State-changing requests require a CSRF token. Most application routes require authentication; Swagger UI documents their success and error responses.

## Tests and quality checks

Run backend checks from `back/`:

| Purpose | Command |
|---|---|
| Fast test suite | `./mvnw test` |
| Unit and integration tests, coverage reports, coverage thresholds, and formatting verification | `./mvnw verify` |
| Generate Javadoc | `./mvnw javadoc:javadoc` |
| Check Java formatting | `./mvnw spotless:check` |
| Apply Java formatting | `./mvnw spotless:apply` |

`verify` runs `*Test` classes with Surefire and `*IT` classes with Failsafe. Integration tests use an isolated MySQL Testcontainers instance, so Docker must be available.

Run frontend checks from `front/`:

| Purpose | Command |
|---|---|
| Unit test suite | `npm test` |
| Vitest unit and integration coverage | `npm run test:coverage` |
| Cypress end-to-end tests | `npm run e2e` |
| Cypress end-to-end coverage | `npm run e2e:coverage` |
| Lint | `npm run lint` |
| Check formatting | `npm run format:check` |
| Apply formatting | `npm run format` |
| Run the frontend SonarQube scan workflow | `npm run sonar` |

Run Bruno checks from the repository root:

| Purpose | Command |
|---|---|
| Start the isolated API and MySQL stack, run the collection, then stop it | `npm run bruno:test` |

Coverage report entry points are available in [`docs/reports/coverage/index.html`](docs/reports/coverage/index.html). Their generation commands and scope are documented in [`docs/reports/coverage/README.md`](docs/reports/coverage/README.md).

## Additional documentation

- [`front/README.md`](front/README.md): frontend architecture, setup, routes, and commands.
- [`back/README.md`](back/README.md): backend architecture, configuration, API, and commands.
- [`docs/javadoc/DOC_CONVENTIONS.md`](docs/javadoc/DOC_CONVENTIONS.md): Java documentation conventions.<br>Generated Javadoc starts at [`docs/javadoc/apidocs/index.html`](docs/javadoc/apidocs/index.html).
- [`docs/reports/coverage/README.md`](docs/reports/coverage/README.md): generated coverage reports and their scope.
- [`bruno/mdd-api/`](bruno/mdd-api/): executable API checks and local environments.

This README documents the configuration, commands, and endpoints currently present in the repository.
