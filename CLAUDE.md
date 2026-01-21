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

### 3. Verify Setup

Run `/mcp` in Claude Code to verify your MCP servers are connected:
- `github` - For PR operations, issues, code search
- `atlassian-jira` - For JIRA tickets and Confluence pages

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

## Security Notes

- **Never commit** `.mcp.json` (contains credentials)
- **Never commit** API keys or tokens in code
- Use environment variables for all secrets
- The `.mcp.json.example` uses `${GITHUB_TOKEN}` syntax for environment variable substitution
