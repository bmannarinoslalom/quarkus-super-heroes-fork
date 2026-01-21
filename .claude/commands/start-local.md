Start or stop the local development environment using Docker Compose.

## Input

`$ARGUMENTS` can be:
- Empty or `full` - Start all backend services (databases, Kafka, microservices)
- `databases` - Start only databases (PostgreSQL, MongoDB, MariaDB)
- `infrastructure` - Start databases + Kafka + Apicurio
- `monitoring` - Start observability stack (Prometheus, Jaeger, OpenTelemetry Collector)
- `all` - Start everything including monitoring
- `stop` - Stop all running containers
- `status` - Show status of running containers
- `logs [service]` - Show logs for a service

## Prerequisites

Before starting, verify Docker is running:
```bash
docker info > /dev/null 2>&1
```

If Docker is not running, inform the user:
"Docker is not running. Please start Docker Desktop or the Docker daemon first."

## Docker Compose Files

| File | Purpose |
|------|---------|
| `deploy/docker-compose/backends.yml` | All backend services + infrastructure |
| `deploy/docker-compose/monitoring.yml` | Observability stack |
| `deploy/docker-compose/java21.yml` | Full stack with UI (CI/CD generated) |

## Profiles and Services

### `databases` Profile
Starts only database containers for local Quarkus development:
```bash
docker compose -f deploy/docker-compose/backends.yml up -d \
  heroes-db villains-db fights-db locations-db
```

**Services:**
| Service | Container | Port | Purpose |
|---------|-----------|------|---------|
| heroes-db | PostgreSQL 16 | 5434 | Heroes data |
| villains-db | PostgreSQL 16 | 5433 | Villains data |
| fights-db | MongoDB 7.0 | 27017 | Fights data |
| locations-db | MariaDB 11.5 | 3306 | Locations data |

### `infrastructure` Profile
Starts databases + messaging (for integration tests):
```bash
docker compose -f deploy/docker-compose/backends.yml up -d \
  heroes-db villains-db fights-db locations-db \
  apicurio fights-kafka
```

**Additional Services:**
| Service | Container | Port | Purpose |
|---------|-----------|------|---------|
| apicurio | Apicurio Registry | 8086 | Schema registry |
| fights-kafka | Kafka (KRaft) | 9092 | Event streaming |

### `full` Profile (default)
Starts all backend services (use when running UI locally with npm):
```bash
docker compose -f deploy/docker-compose/backends.yml up -d
```

**All Services:**
| Service | Port | Health Check |
|---------|------|--------------|
| heroes-db | 5434 | `pg_isready` |
| villains-db | 5433 | `pg_isready` |
| fights-db | 27017 | `mongosh --eval "db.runCommand('ping')"` |
| locations-db | 3306 | `healthcheck` |
| apicurio | 8086 | `http://localhost:8086/health` |
| fights-kafka | 9092 | Connection test |
| rest-heroes | 8083 | `http://localhost:8083/q/health` |
| rest-villains | 8084 | `http://localhost:8084/q/health` |
| rest-fights | 8082 | `http://localhost:8082/q/health` |
| rest-narration | 8087 | `http://localhost:8087/q/health` |
| grpc-locations | 8089 | gRPC health |
| event-statistics | 8085 | `http://localhost:8085/q/health` |

### `monitoring` Profile
Starts observability stack:
```bash
docker compose -f deploy/docker-compose/monitoring.yml up -d
```

**Services:**
| Service | Port | UI URL |
|---------|------|--------|
| prometheus | 9090 | http://localhost:9090 |
| jaeger | 16686 | http://localhost:16686 |
| otel-collector | 4317 | (gRPC endpoint) |

### `all` Profile
Starts everything:
```bash
docker compose -f deploy/docker-compose/backends.yml \
  -f deploy/docker-compose/monitoring.yml up -d
```

## Steps

### For `start` commands:

1. **Check Docker is running**
   ```bash
   docker info > /dev/null 2>&1 || echo "Docker not running"
   ```

2. **Check for existing containers**
   ```bash
   docker compose -f deploy/docker-compose/backends.yml ps
   ```

3. **Start requested services**
   - Use appropriate compose file and service list
   - Add `-d` flag for detached mode

4. **Wait for services to be healthy** (poll every 5 seconds, max 60 seconds)
   - Check container status: `docker compose ps`
   - For Quarkus services, check health endpoint

5. **Display summary** with URLs and status

### For `stop` command:

```bash
docker compose -f deploy/docker-compose/backends.yml \
  -f deploy/docker-compose/monitoring.yml down
```

### For `status` command:

```bash
docker compose -f deploy/docker-compose/backends.yml \
  -f deploy/docker-compose/monitoring.yml ps
```

### For `logs` command:

```bash
docker compose -f deploy/docker-compose/backends.yml logs -f [service]
```

## Output Format

### Startup Success
```
## Local Environment Started

### Profile: {profile}

| Service | Status | URL |
|---------|--------|-----|
| heroes-db | Running | localhost:5434 |
| villains-db | Running | localhost:5433 |
| fights-db | Running | localhost:27017 |
| rest-heroes | Running | http://localhost:8083 |
| rest-villains | Running | http://localhost:8084 |
| rest-fights | Running | http://localhost:8082 |
...

### Quick Links
- Swagger UI (Heroes): http://localhost:8083/q/swagger-ui
- Swagger UI (Villains): http://localhost:8084/q/swagger-ui
- Swagger UI (Fights): http://localhost:8082/q/swagger-ui
- Jaeger UI: http://localhost:16686 (if monitoring enabled)
- Prometheus: http://localhost:9090 (if monitoring enabled)

### Next Steps
- Run the UI locally: `cd ui-super-heroes && npm install && npm start`
- UI will be available at: http://localhost:3000
```

### Stop Success
```
## Local Environment Stopped

All containers have been stopped and removed.
```

### Status Output
```
## Local Environment Status

| Container | Status | Ports |
|-----------|--------|-------|
| heroes-db | Up 5 minutes | 0.0.0.0:5434->5432/tcp |
| villains-db | Up 5 minutes | 0.0.0.0:5433->5432/tcp |
| rest-heroes | Up 3 minutes (healthy) | 0.0.0.0:8083->8083/tcp |
...

{If no containers running}
No containers are currently running.
Use `/start-local` to start the environment.
```

## Error Handling

### Docker Not Running
```
Docker is not running.

Please start Docker:
- **Mac**: Open Docker Desktop
- **Linux**: Run `sudo systemctl start docker`
- **Windows**: Start Docker Desktop from the Start menu

Then retry `/start-local`
```

### Port Already in Use
```
Port {port} is already in use.

Check what's using it:
  lsof -i :{port}

Options:
1. Stop the conflicting process
2. Use `/start-local stop` to clean up previous containers
```

### Service Failed to Start
```
Service {service} failed to start.

Container logs:
{last 20 lines of logs}

Common fixes:
- Check if ports are available
- Ensure Docker has enough memory (4GB+ recommended)
- Try `/start-local stop` then `/start-local` again
```

## Examples

### Start databases for local Quarkus development
```
> /start-local databases

Checking Docker status... OK
Starting databases...

## Local Environment Started

### Profile: databases

| Service | Status | URL |
|---------|--------|-----|
| heroes-db | Running | localhost:5434 |
| villains-db | Running | localhost:5433 |
| fights-db | Running | localhost:27017 |
| locations-db | Running | localhost:3306 |

### Next Steps
Run Quarkus services locally with:
  cd rest-heroes && ./mvnw quarkus:dev
  cd rest-villains && ./mvnw quarkus:dev
```

### Start full backend stack
```
> /start-local

Checking Docker status... OK
Starting all backend services...
Waiting for services to be healthy...

## Local Environment Started

### Profile: full

| Service | Status | URL |
|---------|--------|-----|
| heroes-db | Running | localhost:5434 |
| villains-db | Running | localhost:5433 |
| fights-db | Running | localhost:27017 |
| locations-db | Running | localhost:3306 |
| apicurio | Running | http://localhost:8086 |
| fights-kafka | Running | localhost:9092 |
| rest-heroes | Running | http://localhost:8083 |
| rest-villains | Running | http://localhost:8084 |
| rest-fights | Running | http://localhost:8082 |
| rest-narration | Running | http://localhost:8087 |
| grpc-locations | Running | localhost:8089 |
| event-statistics | Running | http://localhost:8085 |

### Quick Links
- Swagger UI (Fights): http://localhost:8082/q/swagger-ui
- API Health: http://localhost:8082/q/health

### Next Steps
Run the UI locally:
  cd ui-super-heroes && npm install && npm start
  Open http://localhost:3000
```

### Stop everything
```
> /start-local stop

Stopping all containers...

## Local Environment Stopped

All containers have been stopped and removed.
```

## Important Notes

- The `backends.yml` file is designed for local development with the UI running separately via npm
- Quarkus services take 10-30 seconds to start depending on your machine
- First startup is slower due to Docker image downloads
- Use `databases` profile for fastest startup when running Quarkus in dev mode
- The `rest-narration` service requires `OPENAI_API_KEY` env var for real AI narrations
