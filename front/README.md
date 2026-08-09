# MDD frontend (Monde Du Dév)

This directory contains the Angular single-page application for MDD.

## Target frontend baseline

The frontend will be modernized to Angular 22 with strict TypeScript, standalone components, zoneless change detection, and SCSS. PrimeNG will first be evaluated through a focused accessibility and responsive-navigation spike; Angular Material remains the fallback.

The current scaffold still uses Angular 14 and Karma/Jasmine. Its commands remain available during the migration. Vitest will replace the current unit-test runner as part of the technical baseline work.

## Prerequisites

Install a supported Node.js version before installing dependencies. The exact Node.js version will be documented when Angular 22 is installed.

## Install dependencies

Run the command from this directory:

```bash
npm install
```

## Commands

| Purpose | Command | Result |
|---|---|---|
| Start the development server | `npm start` | Runs the application with live reload |
| Build the application | `npm run build` | Creates the production build in `dist/front/` |
| Build continuously | `npm run watch` | Watches source files using the development configuration |
| Run the current test suite | `npm test` | Runs the legacy Karma/Jasmine test suite until the Vitest migration |

## Important URLs

| Service | URL |
|---|---|
| Development application | `http://localhost:4200/` |
| Planned backend API | `http://localhost:8080/api` |
| Planned Swagger UI | `http://localhost:8080/swagger-ui/index.html` |

## Related documentation

The repository-level target technical baseline is documented in [`../README.md`](../README.md).

This README will evolve as the frontend technical foundation is implemented.
