# Architecture Standards

## Microservices
- **rest-villains** (8084): Villain CRUD, PostgreSQL, blocking
- **rest-heroes** (8083): Hero CRUD, PostgreSQL, reactive
- **rest-fights** (8082): Fight orchestration, MongoDB, reactive
- **rest-narration** (8087): AI narration, OpenAI/Azure OpenAI
- **grpc-locations** (8089): Location service, gRPC, MariaDB, Kotlin
- **event-statistics** (8085): Kafka consumer, WebSocket stats
- **ui-super-heroes** (8080): React UI, Quinoa

## Service Discovery
- Use SmallRye Stork for client-side load balancing
- Static list in dev/docker-compose
- Kubernetes service discovery in production
- Configure via `quarkus.stork.{service-name}.service-discovery`

## Communication Patterns
- **Synchronous**: REST (heroes, villains, narration), gRPC (locations)
- **Asynchronous**: Kafka (fight events to statistics)
- **Real-time**: WebSockets (statistics to UI)

## Data Storage
- PostgreSQL: Heroes, Villains
- MongoDB: Fights
- MariaDB: Locations
- Kafka: Event streaming
- Apicurio: Schema registry

## Resiliency
- Timeouts on all external calls
- Fallbacks for degraded functionality
- Retries with exponential backoff
- Circuit breakers for failing services

## Observability
- OpenTelemetry for distributed tracing
- Prometheus metrics
- Jaeger for trace visualization
- All services export to OTLP collector

## Security
- No authentication in demo (add for production)
- Substitute PII in examples
- Never commit credentials
- Use environment variables for secrets

## API Design
- REST: `/api/{resource}` pattern
- OpenAPI documentation auto-generated
- Use proper HTTP status codes
- Return appropriate error responses

## Deployment
- Docker Compose for local dev
- Kubernetes/OpenShift for production
- Knative for serverless scaling
- Pre-built images at quay.io/quarkus-super-heroes
