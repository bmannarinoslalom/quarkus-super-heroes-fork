# Developer Onboarding Guide

Welcome to the Quarkus Super Heroes project! This guide will help you get started with Claude Code and the development workflow.

## Quick Start Checklist

- [ ] Clone the repository
- [ ] Install prerequisites (Java 17+, Maven, Docker, Node.js)
- [ ] Set up Claude Code MCP servers
- [ ] Verify MCP connections
- [ ] Try your first `/implement-ticket` command

---

## 1. Prerequisites

### Required Software

| Software | Version | Purpose |
|----------|---------|---------|
| Java | 17+ | Backend services |
| Maven | 3.8+ | Build tool |
| Docker | Latest | Infrastructure containers |
| Node.js | 18+ | UI development |
| Claude Code | Latest | AI-assisted development |

### Required Accounts

- **GitHub** - Repository access
- **Atlassian** - JIRA ticket access
- **Slack** - Team notifications (optional)

---

## 2. Claude Code Setup

### Step 1: Install Claude Code

```bash
# Install via npm
npm install -g @anthropic-ai/claude-code

# Or via Homebrew (macOS)
brew install claude-code
```

### Step 2: Configure MCP Servers

```bash
# Navigate to project root
cd quarkus-super-heroes

# Copy example config
cp .mcp.json.example .mcp.json

# Set environment variables
export GITHUB_TOKEN="ghp_your_token_here"
export SLACK_BOT_TOKEN="xoxb-your-token"  # Optional
export SLACK_TEAM_ID="T0AA178HMPG"        # Optional
```

**Getting a GitHub Token:**
1. Go to https://github.com/settings/tokens
2. Click "Generate new token (classic)"
3. Select scopes: `repo`, `read:org`, `read:user`
4. Copy the token

### Step 3: Verify Setup

```bash
# Start Claude Code
claude

# Check MCP connections
/mcp
```

You should see:
- `github` - Connected
- `atlassian-jira` - Connected (will prompt for OAuth on first use)
- `slack` - Connected (if configured)

---

## 3. Development Workflow

### Standard Workflow with Claude Code

```mermaid
graph LR
    A[Pick JIRA Ticket] --> B[/implement-ticket]
    B --> C[Claude writes tests]
    C --> D[Claude implements]
    D --> E[Tests pass?]
    E -->|Yes| F[PR Created]
    E -->|No| D
    F --> G[/review-pr]
    G --> H[Address feedback]
    H --> I[Merge]
```

### Example: Implementing a Feature

```bash
# 1. Start Claude Code in the project directory
cd quarkus-super-heroes
claude

# 2. Implement a JIRA ticket
/implement-ticket SCRUM-123

# Claude will:
# - Read the ticket from JIRA
# - Create a feature branch
# - Write tests first (TDD)
# - Implement the code
# - Run tests
# - Create a PR
# - Notify Slack
```

### Example: Reviewing a PR

```bash
# Review a specific PR
/review-pr 5

# Claude will:
# - Fetch PR details from GitHub
# - Analyze code against project standards
# - Submit review with inline comments
# - Post notification to Slack
```

### Example: Manual Slack Notification

```bash
# Send a PR notification to Slack
/notify-pr 5
```

---

## 4. Project Structure

```
quarkus-super-heroes/
├── rest-heroes/          # Hero microservice
├── rest-villains/        # Villain microservice
├── rest-fights/          # Fight orchestration
├── rest-narration/       # AI narration
├── event-statistics/     # Kafka consumer
├── grpc-locations/       # gRPC service
├── ui-super-heroes/      # React frontend
├── deploy/               # Deployment configs
└── .claude/              # Claude Code config
    ├── commands/         # Skill definitions
    │   ├── implement-ticket.md
    │   ├── review-pr.md
    │   └── notify-pr.md
    ├── docs/             # Documentation
    │   ├── architecture.md
    │   ├── claude-code-integration.md
    │   ├── onboarding.md
    │   └── pr-review-standards.md
    └── settings.json
```

---

## 5. Code Conventions

### Java/Quarkus

```java
// Use constructor injection (NOT field @Inject)
@ApplicationScoped
public class HeroService {
    private final HeroRepository repository;

    public HeroService(HeroRepository repository) {
        this.repository = repository;
    }

    // Use reactive types
    public Uni<Hero> findById(Long id) {
        return repository.findById(id);
    }

    // Add tracing to important methods
    @WithSpan
    public Uni<List<Hero>> findAll() {
        return repository.listAll();
    }
}
```

### REST Endpoints

```java
@Path("/api/heroes")
@Tag(name = "Heroes")
@ApplicationScoped
public class HeroResource {

    @GET
    @Path("/{id}")
    @Operation(summary = "Get hero by ID")
    @APIResponse(responseCode = "200", description = "Hero found")
    @APIResponse(responseCode = "404", description = "Hero not found")
    public Uni<Response> getHero(@PathParam("id") @NotNull Long id) {
        // Implementation
    }
}
```

### Git Workflow

- **Branch naming:** `feature/SCRUM-XXX-short-description`
- **Commit messages:** `feat(heroes): add power level field [SCRUM-123]`
- **Always reference JIRA ticket** in commits and PR descriptions

---

## 6. Running Services Locally

### Start Infrastructure

```bash
cd deploy/docker-compose
docker-compose -f infrastructure.yml up -d
```

### Start a Service in Dev Mode

```bash
# Heroes service
./mvnw quarkus:dev -f rest-heroes/pom.xml

# Villains service
./mvnw quarkus:dev -f rest-villains/pom.xml

# Fights service
./mvnw quarkus:dev -f rest-fights/pom.xml
```

### Run Tests

```bash
# All tests for a service
./mvnw verify -f rest-heroes/pom.xml

# Specific test class
./mvnw test -f rest-heroes/pom.xml -Dtest=HeroResourceTests
```

---

## 7. Troubleshooting

### MCP Server Won't Connect

```bash
# Check status
/mcp

# Reconnect specific server
/mcp  # Then select server
```

### Atlassian 401 Unauthorized

1. Run `/mcp`
2. Select `atlassian-jira`
3. Complete OAuth flow in browser
4. Return to Claude Code

### Slack "not_in_channel" Error

In Slack, go to the channel and run:
```
/invite @MCP Bot
```

### Tests Failing in CI

```bash
# Run tests locally first
./mvnw verify -f rest-heroes/pom.xml

# Check for environment-specific issues
./mvnw verify -f rest-heroes/pom.xml -Dquarkus.test.profile=test
```

---

## 8. Getting Help

- **Claude Code Issues:** https://github.com/anthropics/claude-code/issues
- **Project Questions:** Ask in Slack `#dev-help`
- **JIRA Access:** Contact your team lead

---

## Next Steps

1. Pick a ticket from the backlog
2. Run `/implement-ticket SCRUM-XXX`
3. Let Claude guide you through the implementation
4. Get your first PR merged!

Happy coding! :rocket:
