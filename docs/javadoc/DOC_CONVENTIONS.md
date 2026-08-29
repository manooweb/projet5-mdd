# Documentation conventions

## JavaDoc scope

The backend documents its public, non-trivial API:

- REST controllers and their endpoints;
- services containing business or security logic;
- API request and response DTOs, including validation constraints;
- custom API errors and security components;
- configuration classes that expose application behaviour.

Each relevant method states its purpose and, where applicable, its parameters,
return value, preconditions, HTTP outcome or security implications. Links to
related types are added when they improve navigation in the generated API.

## Intentional exclusions

Generated Lombok accessors, record accessors, constructors used only for
dependency injection, and ordinary entity getters are not documented
individually. Their names and types are self-explanatory, while their
non-trivial domain behaviour is documented at the class or method level.

## Generation

Run the following command from `back`:

```bash
./mvnw javadoc:javadoc
```

The generated HTML API is written to `docs/javadoc/apidocs`. It is regenerated
after documentation changes and is not hand-edited. The surrounding
`docs/javadoc` directory is reserved for Javadoc-related source documentation.
