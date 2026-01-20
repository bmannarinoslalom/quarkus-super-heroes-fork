# Java/Quarkus Standards

## Dependency Injection
- **rest-villains**: Use FIELD injection with `@Inject`
- **rest-heroes**: Use CONSTRUCTOR injection (no `@Inject` on fields)
- **rest-fights**: Use CONSTRUCTOR injection
- Be consistent within each service

## REST Endpoints
- Use `@Path("/api/{resource}")` pattern
- Reactive services: Use RESTEasy Reactive with `Uni<>` return types
- Blocking services: Use standard JAX-RS with direct returns
- Name classes `{Resource}Resource` (e.g., `FightResource`, `VillainResource`)

## Panache Patterns
- **Active Record**: `rest-villains`, `rest-fights` - entities extend `PanacheEntity` or `PanacheMongoEntity`
- **Repository**: `rest-heroes` - separate repository classes with `PanacheRepository<T>`
- Use `findRandom()` for random entity selection

## Package Structure
```
io.quarkus.sample.superheroes.{service}/
├── {Entity}.java           # Domain model
├── rest/
│   └── {Entity}Resource.java
├── service/
│   └── {Entity}Service.java
├── client/                 # External service clients
└── config/                 # Configuration classes
```

## Fault Tolerance
- Use `@Timeout`, `@Fallback`, `@Retry`, `@CircuitBreaker` from SmallRye Fault Tolerance
- Place on service methods, not REST endpoints
- Always provide fallback methods for external calls

## Testing
- Unit tests: `@QuarkusTest`
- Integration tests: Suffix with `IT`, use `@QuarkusIntegrationTest`
- Use Wiremock for mocking HTTP services
- Use Dev Services for databases/Kafka
- Test method naming: `testMethodName_SCRUM{number}` for traceability
- All tests require SCRUM ticket reference in method name or comment

## Configuration
- Store in `application.properties` or `application.yml`
- Use `@ConfigMapping` for type-safe config
- Prefix properties with service name (e.g., `fight.villain.client-base-url`)

## Reactive vs Blocking
- MongoDB: Use reactive (`Uni<>`, `Multi<>`)
- PostgreSQL: Can be reactive or blocking depending on service
- REST clients: Match the service style (reactive for reactive services)
