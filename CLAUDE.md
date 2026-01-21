# Claude Code Configuration

This document provides instructions for setting up and using Claude Code with the Quarkus Super Heroes project.

## Quick Start

### 1. Set Up MCP Servers

Copy the example configuration and set your GitHub token:

```bash
# Copy the example config
cp .mcp.json.example .mcp.json

# Set your GitHub token (create one at https://github.com/settings/tokens)
# The token needs: repo, read:org, read:user permissions
export GITHUB_TOKEN="your-github-token-here"
```

**Important:** Never commit `.mcp.json` - it contains credentials and is gitignored.

### 2. Authenticate Atlassian (First Time)

When you first use an Atlassian command, Claude Code will open a browser for OAuth authentication. Follow the prompts to authorize access.

### 3. Set Up Slack (Optional)

To enable Slack notifications for PR events:

```bash
# Set your Slack bot token and team ID
export SLACK_BOT_TOKEN="xoxb-your-bot-token"
export SLACK_TEAM_ID="T01234567"  # Your workspace ID (from Slack URL)
```

**Creating a Slack Bot:**
1. Go to [api.slack.com/apps](https://api.slack.com/apps) and create a new app
2. Under **OAuth & Permissions**, add these Bot Token Scopes:
   - `chat:write` - Send messages
   - `channels:read` - View channel info
3. Install the app to your workspace
4. Copy the **Bot User OAuth Token** (starts with `xoxb-`)
5. Find your **Team ID** from your Slack workspace URL (e.g., `T0AA178HMPG`)
6. Invite the bot to your notification channel: `/invite @YourBotName`

### 4. Verify Setup

Run `/mcp` in Claude Code to verify your MCP servers are connected:
- `github` - For PR operations, issues, code search
- `atlassian-jira` - For JIRA tickets and Confluence pages
- `slack` - For Slack notifications (optional)

## Available Skills

### `/implement-ticket <TICKET-ID>`

Implements a JIRA ticket end-to-end:
1. Fetches ticket details from JIRA
2. Creates a feature branch
3. Writes tests first (TDD)
4. Implements the changes
5. Runs tests and commits
6. Creates a PR and links it to the ticket

**Example:** `/implement-ticket SCRUM-123`

### `/review-pr <PR-NUMBER>`

Reviews a pull request against project standards:
1. Fetches PR details and changed files
2. Analyzes code against standards in `.claude/docs/pr-review-standards.md`
3. Automatically submits review with inline comments
4. Uses severity indicators: 🔴 critical, 🟡 important, 💡 suggestion

**Example:** `/review-pr 5`

### `/notify-pr <PR-NUMBER>`

Sends a Slack notification about a PR to the #pull-requests channel:
1. Fetches PR details from GitHub
2. Formats a rich message based on PR state (open, merged, closed)
3. Posts to the configured Slack channel

**Example:** `/notify-pr 5`

**Note:** Requires Slack MCP to be configured (see setup instructions above).

### `/run-tests [SERVICE] [FLAGS]`

Runs tests for the project and reports results:
1. Detects changed modules (or uses specified service)
2. Runs Maven tests with appropriate flags
3. Parses test results and displays summary
4. Shows failed test details with file locations

**Arguments:**
- Empty: Run tests for modules with uncommitted changes
- Service name: `rest-heroes`, `rest-villains`, `rest-fights`, `rest-narration`, `grpc-locations`, `event-statistics`
- `all`: Run tests for all modules

**Flags:**
- `-it` or `--integration`: Run only integration tests
- `-ut` or `--unit`: Run only unit tests

**Examples:**
```bash
/run-tests                    # Test changed modules
/run-tests rest-heroes        # Test hero service
/run-tests all                # Test everything
/run-tests rest-fights -it    # Integration tests only
```

### `/start-local [PROFILE]`

Starts or stops the local development environment using Docker Compose:
1. Checks Docker is running
2. Starts requested services based on profile
3. Waits for services to be healthy
4. Displays service status and URLs

**Profiles:**
- Empty or `full` - Start all backend services (databases, Kafka, microservices)
- `databases` - Start only databases (PostgreSQL, MongoDB, MariaDB)
- `infrastructure` - Start databases + Kafka + Apicurio
- `monitoring` - Start observability stack (Prometheus, Jaeger, OpenTelemetry)
- `all` - Start everything including monitoring

**Commands:**
- `stop` - Stop all running containers
- `status` - Show status of running containers
- `logs [service]` - Show logs for a service

**Examples:**
```
/start-local              # Start full backend stack
/start-local databases    # Start only databases for local Quarkus dev
/start-local stop         # Stop all containers
/start-local status       # Check what's running
```

### `/sync-docs [SCOPE]`

Updates Confluence documentation to reflect recent changes in the codebase:
1. Detects what changed (auto-detect or specify scope)
2. Regenerates architecture diagrams using Mermaid MCP
3. Updates relevant Confluence pages
4. Commits and pushes diagram changes

**Scopes:**
- `all` - Update all documentation
- `architecture` - Update architecture diagrams and service details
- `claude` - Update Claude Code integration docs
- `deployment` - Update deployment architecture docs
- (empty) - Auto-detect based on recent commits

**Examples:**
```
/sync-docs              # Auto-detect and update affected docs
/sync-docs architecture # Update only architecture docs
/sync-docs claude       # Update only Claude Code docs
```

## Project Conventions

When working on this codebase, Claude should follow these conventions:

### Java/Quarkus Backend

- **Indentation:** 2 spaces (see `.editorconfig`)
- **Dependency Injection:** Constructor injection, not field `@Inject`
- **Bean Scope:** `@ApplicationScoped` for services
- **Reactive:** Use `Uni<T>` and `Multi<T>` from Mutiny
- **Logging:** Use `Log.debugf()`, `Log.infof()` (Quarkus logging)
- **Tracing:** Add `@WithSpan` to important methods
- **REST:** Include OpenAPI annotations (`@Operation`, `@APIResponse`, `@Tag`)
- **Validation:** Use `@Valid`, `@NotNull`, `@NotBlank` on inputs

### React UI

- **Components:** Functional components with hooks (no class components)
- **Testing:** Use `@testing-library/react`, mock API calls
- **No console.log** in production code

### Testing

- **Unit tests:** `*Tests.java`
- **Integration tests:** `*IT.java` with `@QuarkusTest`
- **REST tests:** Use REST Assured
- **UI tests:** Jest with React Testing Library

### Git Workflow

- **Branch naming:** `feature/SCRUM-XXX-short-description` or `bugfix/SCRUM-XXX-short-description`
- **Commit messages:** `feat(scope): description [SCRUM-XXX]` or `fix(scope): description [SCRUM-XXX]`
- **Always reference JIRA ticket** in commits and PR descriptions

## Project Structure

```
quarkus-super-heroes/
├── rest-heroes/          # Hero microservice (Quarkus + PostgreSQL)
├── rest-villains/        # Villain microservice (Quarkus + PostgreSQL)
├── rest-fights/          # Fight orchestration service (Quarkus + MongoDB)
├── rest-narration/       # AI narration service (Quarkus + LangChain4j)
├── event-statistics/     # Kafka consumer for fight statistics
├── grpc-locations/       # gRPC location service (Quarkus + MariaDB)
├── ui-super-heroes/      # React frontend
├── deploy/               # Docker Compose and Kubernetes configs
└── .claude/              # Claude Code configuration
    ├── commands/         # Skill definitions
    ├── docs/             # Reference documentation
    └── settings.json     # Project settings
```

## Troubleshooting

### MCP Server Issues

```bash
# Check MCP server status
/mcp

# Reconnect a specific server
/mcp  # Then select the server to reconnect
```

### Atlassian Authentication

If Atlassian commands fail with 401 Unauthorized:
1. Run `/mcp` and reconnect `atlassian-jira`
2. Complete the OAuth flow in your browser
3. Return to Claude Code and retry

### GitHub Token Issues

If GitHub operations fail:
1. Verify your `GITHUB_TOKEN` environment variable is set
2. Check the token has required permissions: `repo`, `read:org`, `read:user`
3. Regenerate the token if expired

### Slack Issues

If Slack notifications fail:
1. Verify `SLACK_BOT_TOKEN` and `SLACK_TEAM_ID` environment variables are set
2. Check the bot is installed in your workspace
3. Ensure the bot is invited to the target channel (`/invite @BotName`)
4. Verify bot has `chat:write` and `channels:read` scopes

## Security Notes

- **Never commit** `.mcp.json` (contains credentials)
- **Never commit** API keys or tokens in code
- Use environment variables for all secrets
- The `.mcp.json.example` uses `${GITHUB_TOKEN}` and `${SLACK_BOT_TOKEN}` syntax for environment variable substitution
