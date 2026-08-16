# MDD frontend (Monde Du Dév)

This directory contains the Angular single-page application for MDD.

## Technical baseline

The frontend uses Angular 22, strict TypeScript, standalone components, zoneless change detection, SCSS, and Vitest. The application is organized by feature: the current homepage is located in `src/app/home/`.

PrimeNG will be evaluated through a focused accessibility and responsive-navigation spike. Angular Material is not part of the current frontend dependencies.

## Prerequisites

Use Node.js `22.22.3` or later in the 22.x line, then install dependencies from this directory.

```bash
npm install
```

## Commands

| Purpose                      | Command         | Result                                                   |
| ---------------------------- | --------------- | -------------------------------------------------------- |
| Start the development server | `npm start`     | Runs the application with live reload                    |
| Build the application        | `npm run build` | Creates the production build in `dist/front/`            |
| Build continuously           | `npm run watch` | Watches source files using the development configuration |
| Run unit tests               | `npm test`      | Runs the Vitest unit-test suite                          |

## Development proxy

The development server proxies requests beginning with `/api` to the local Spring Boot backend. This keeps development requests same-origin and avoids CORS configuration for the MVP.

| Service                       | URL                                           |
| ----------------------------- | --------------------------------------------- |
| Development application       | `http://localhost:4400/`                      |
| Backend API through the proxy | `http://localhost:4400/api`                   |
| Local Spring Boot API         | `http://localhost:9001/api`                   |
| Local Swagger UI              | `http://localhost:9001/swagger-ui/index.html` |

## Related documentation

The repository-level target technical baseline is documented in [`../README.md`](../README.md).

This README will evolve as the frontend technical foundation is implemented.
