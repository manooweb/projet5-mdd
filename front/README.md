# MDD frontend (Monde Du Dév)

This directory contains the Angular single-page application for MDD. It provides the French user interface for registration, authentication, topic subscriptions, the article feed, article creation and detail, comments, and profile management.

## Architecture

The application uses Angular 22, strict TypeScript, standalone components, zoneless change detection, SCSS, PrimeNG, and Tailwind CSS.

Source code is organized by feature:

| Directory | Responsibility |
|---|---|
| `src/app/auth/` | Registration, login, session service, guards, and session-expiration handling |
| `src/app/topic/` | Topic list and subscription actions |
| `src/app/post/` | Article feed, article creation, article detail, and comments |
| `src/app/user/` | Current user profile display and update |
| `src/app/shared/` | Reusable navigation, form, feedback, icon, and layout components |
| `src/app/home/` | Public landing page |

Typed HTTP services communicate with the REST API. The browser never reads the `HttpOnly` authentication cookie. Instead, the application restores the session through `GET /api/users/me`, and the route guard protects member-only screens. Angular's XSRF configuration reads the `XSRF-TOKEN` cookie and sends the `X-XSRF-TOKEN` header for state-changing API requests.

## Application routes

| Route | Access | Screen |
|---|---|---|
| `/` | Public | Landing page |
| `/login` | Public | Login form |
| `/register` | Public | Registration form |
| `/posts` | Authenticated | Article feed |
| `/posts/create` | Authenticated | Article creation form |
| `/posts/:postId` | Authenticated | Article and comments |
| `/topics` | Authenticated | Topic subscriptions |
| `/profile` | Authenticated | Profile update form and topic unsubscription |

## Prerequisites and installation

Use Node.js `22.22.3` or later in the 22.x line, then install dependencies from this directory:

```bash
npm install
```

The local backend must also be running for API-driven screens. See the repository [README](../README.md) for backend configuration and startup.

## Commands

| Purpose | Command | Result |
|---|---|---|
| Start the development server | `npm start` | Runs the application with live reload on port 4400 |
| Build the application | `npm run build` | Creates the production build in `dist/front/` |
| Build continuously | `npm run watch` | Watches source files with the development configuration |
| Run unit tests | `npm test` | Runs the Vitest unit-test suite |
| Generate combined Vitest coverage | `npm run test:coverage` | Runs unit and integration suites, then merges their reports |
| Run lint | `npm run lint` | Checks TypeScript and Angular templates |
| Check formatting | `npm run format:check` | Checks Prettier formatting |
| Apply formatting | `npm run format` | Applies Prettier formatting |
| Open Cypress | `npm run cypress:open` | Opens the Cypress runner without coverage instrumentation |
| Run Cypress | `npm run e2e` | Starts the development server and runs Cypress scenarios |
| Generate Cypress coverage | `npm run e2e:coverage` | Builds the instrumented application, runs Cypress on port 4201, and generates coverage |
| Run SonarQube workflow | `npm run sonar` | Regenerates Vitest and Cypress coverage, then starts the configured scan |

## Development proxy

The development server proxies requests beginning with `/api` to the local Spring Boot backend. This keeps development requests same-origin and avoids CORS configuration for the MVP.

| Service | URL |
|---|---|
| Development application | `http://localhost:4400/` |
| Backend API through the proxy | `http://localhost:4400/api` |
| Local Spring Boot API | `http://localhost:9001/api` |
| Local Swagger UI | `http://localhost:9001/swagger-ui/index.html` |

## Tests and coverage

Vitest separates unit and integration specifications, then merges them into a combined report. Cypress validates complete user journeys in a browser, including authenticated navigation and main application flows. The generated reports are tracked in the repository:

- [`../docs/reports/coverage/front-vitest/index.html`](../docs/reports/coverage/front-vitest/index.html): combined Vitest coverage.
- [`../docs/reports/coverage/front-e2e/index.html`](../docs/reports/coverage/front-e2e/index.html): Cypress end-to-end coverage.
- [`../docs/reports/coverage/README.md`](../docs/reports/coverage/README.md): report scope and regeneration commands.

## Related documentation

The repository [README](../README.md) documents the complete stack, backend setup, API, security model, Bruno checks, and quality commands.
