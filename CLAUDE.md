# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

This is a multi-service Quarkus application. Each service is its own sub-directory with independent Maven builds (not a multi-module project from the parent).

### Running a Single Service (Dev Mode)
From any service directory (e.g., `rest-heroes/`, `rest-villains/`):
```bash
./mvnw quarkus:dev
# or using Quarkus CLI
quarkus dev
```

### Build a Single Service
```bash
./mvnw clean package
# Skip tests
./mvnw clean package -DskipTests
```

### Run Tests for a Single Service
```bash
./mvnw test                    # Unit tests only
./mvnw verify                  # Unit + integration tests
./mvnw test -Dtest=ClassName   # Single test class
```

### Native Build
```bash
./mvnw clean package -Pnative -DskipTests
```

### Running the Full System via Docker Compose
From the repository root:
```bash
docker compose -f deploy/docker-compose/java21.yml up --remove-orphans
# With monitoring (Prometheus, Jaeger)
docker compose -f deploy/docker-compose/java21.yml -f deploy/docker-compose/monitoring.yml up --remove-orphans
```

## Architecture Overview

A microservices sample application where superheroes fight supervillains. Java 21 is the base JVM version.

### Services and Ports

| Service | Port | Language | Database | Protocol |
|---------|------|----------|----------|----------|
| ui-super-heroes | 8080 | React (Quinoa) | - | HTTP |
| rest-fights | 8082 | Java | MongoDB | REST |
| rest-heroes | 8083 | Java | PostgreSQL | REST (reactive) |
| rest-villains | 8084 | Java | PostgreSQL | REST (blocking) |
| event-statistics | 8085 | Java | - | WebSocket/Kafka |
| rest-narration | 8087 | Java | - | REST + LangChain4j |
| grpc-locations | 8089 | Kotlin | MariaDB | gRPC |

### Service Communication Flow
```
ui-super-heroes → rest-fights → rest-heroes (REST)
                             → rest-villains (REST)
                             → grpc-locations (gRPC)
                             → rest-narration (REST/AI)
                             → Kafka → event-statistics
```

### Key Design Patterns by Service

- **rest-heroes**: Reactive endpoints with Hibernate Reactive Panache (repository pattern), constructor injection
- **rest-villains**: Blocking endpoints with Hibernate ORM Panache (active record pattern), field injection (`@Inject`)
- **rest-fights**: Reactive with MongoDB Panache, SmallRye Fault Tolerance (timeouts, fallbacks, retries), SmallRye Stork for service discovery, Kafka with Avro/Apicurio Schema Registry
- **rest-narration**: LangChain4j for OpenAI/Azure OpenAI integration, WireMock in dev mode for mock responses
- **grpc-locations**: Kotlin with gRPC, Hibernate ORM Panache (repository pattern)
- **event-statistics**: Kafka consumer with SmallRye Reactive Messaging, WebSockets for real-time stats

### Testing

- Unit tests: JUnit 5, Mockito, AssertJ, REST Assured
- Contract tests: Pact (contracts in `src/test/resources/pacts/`)
- UI tests: Playwright (rest-heroes, rest-villains)
- Integration tests: Use Quarkus Dev Services (auto-provision databases, Kafka, etc.)
- Microcks: API mocking in dev mode for rest-fights and ui-super-heroes

### Testing Requirements for All Changes

**Every code or configuration change MUST include tests that verify the change works.**

#### What to Test

| Change Type | Required Tests |
|-------------|----------------|
| New feature | Unit tests + integration tests |
| Bug fix | Test that reproduces the bug (fails before fix, passes after) |
| Configuration change | Integration test verifying config is applied correctly |
| Docker/deployment changes | Integration test or documented manual verification steps |

#### Test Patterns

```java
// Unit test (fast, isolated)
@Test
void shouldDoSomething() { ... }

// Integration test with Quarkus (loads full app context)
@QuarkusTest
class SomethingIT {
    @Test
    void shouldIntegrateCorrectly() { ... }
}

// Config validation test
@QuarkusTest
class ConfigIT {
    @ConfigProperty(name = "some.config.key")
    String configValue;

    @Test
    void configShouldBeSet() {
        assertThat(configValue).isEqualTo("expected");
    }
}
```

#### Running Tests

```bash
./mvnw test                    # Unit tests only
./mvnw verify                  # Unit + integration tests
./mvnw test -Dtest=*IT         # Integration tests only
```

### Configuration

- Most services use `application.properties` (rest-villains, rest-fights, event-statistics, rest-narration)
- Some use `application.yml` (rest-heroes, grpc-locations)
- Profiles: `kubernetes`, `openshift`, `minikube`, `knative`, `openai`, `azure-openai`

### AI/Narration Service

The rest-narration service uses LangChain4j. By default, it returns mock responses (WireMock in dev, fallback in prod). To enable live OpenAI calls:
```bash
quarkus dev -Dquarkus.profile=openai -Dquarkus.langchain4j.openai.api-key=YOUR_KEY
```
