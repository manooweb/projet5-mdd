# Bruno API workflows

The `mdd-api` collection serves two purposes with the same request files:

- Manual exploration of the local application, similar to using Swagger UI.
- Repeatable API checks against a temporary isolated stack.

The request files use environment variables. Do not change a request body or its assertions just to perform a manual experiment; select the appropriate environment instead.

## Environments

| Environment | Target | Intended use |
|---|---|---|
| `Manual` | `http://localhost:9001` | Send individual requests from the Bruno desktop application to the local development database |
| `Isolated` | `http://localhost:9002` | Run the complete collection from the command line against the temporary Docker Compose stack |

`Manual` and `Isolated` use separate application accounts. Neither overlaps with the Flyway demo account. Both environments use the topic identifiers created by the topic seed migration.

## Manual workflow

1. Start the backend normally from `back/` with `./mvnw spring-boot:run`.
2. Open `bruno/mdd-api` with the Bruno desktop application.
3. Select the `Manual` environment.
4. Send only the request you want to inspect or use.

The collection pre-request script initializes CSRF automatically for state-changing requests. Cookies are retained by Bruno's cookie jar, so a successful registration or login authenticates subsequent protected requests.

The versioned manual environment starts with the `bruno-manual` account values. Sending **Register** a second time returns `409 Conflict`, which is expected because the account already exists. To create another account manually, temporarily change `registrationUsername`, `registrationEmail`, `loginIdentifier`, `expectedUsername`, `expectedEmail`, `updateUsername`, and `updateEmail` together in the `Manual` environment.

`postId` is a placeholder for manual use. After creating an article, obtain its identifier from the article list and update `postId` before sending **Get Post** or **Create Comment**. The isolated scenario uses `postId: 1` because it starts with a fresh database.

Do not run the complete collection against `Manual` from the command line. It creates and modifies data in the development database.

## Isolated workflow

Run the complete deterministic scenario from the repository root:

```bash
npm run bruno:test
```

The command builds and starts `compose.bruno.yaml`, waits for the API health endpoint on port `9002`, runs the collection with the `Isolated` environment, then removes the temporary services and volumes. It does not use or modify the local development database.

The scenario registers and authenticates `bruno-isolated`, subscribes to a seeded topic, creates an article and a comment, verifies the resulting resources, updates the profile, and logs out.

## Shared variables

The environments provide the variables used by the common requests:

| Variable group | Examples |
|---|---|
| Target | `baseUrl` |
| Account | `registrationUsername`, `registrationEmail`, `loginIdentifier`, `password` |
| Expected profile | `expectedUsername`, `expectedEmail`, `updateUsername`, `updateEmail`, `updatePassword` |
| Article and comment | `topicId`, `postTitle`, `postContent`, `postId`, `commentContent` |

Only the environment values change between manual use and the isolated scenario. The request definitions, CSRF handling, and API assertions remain shared.
