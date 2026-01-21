# Claude Code Integration

This document explains how Claude Code integrates with external services via MCP (Model Context Protocol) and documents the available skills.

## MCP Architecture

```mermaid
graph LR
    subgraph "Claude Code"
        CC[Claude Code CLI]
        SKILLS[Skills Engine]
    end

    subgraph "MCP Servers"
        GH[GitHub MCP<br/>@modelcontextprotocol/server-github]
        JIRA[Atlassian MCP<br/>mcp.atlassian.com]
        SLACK[Slack MCP<br/>@modelcontextprotocol/server-slack]
    end

    subgraph "External Services"
        GITHUB[GitHub API]
        ATLASSIAN[JIRA/Confluence API]
        SLACK_API[Slack API]
    end

    CC --> GH
    CC --> JIRA
    CC --> SLACK
    SKILLS --> CC

    GH --> GITHUB
    JIRA --> ATLASSIAN
    SLACK --> SLACK_API
```

## MCP Server Configuration

The `.mcp.json` file configures the MCP servers:

```json
{
  "mcpServers": {
    "github": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-github"],
      "env": {
        "GITHUB_PERSONAL_ACCESS_TOKEN": "${GITHUB_TOKEN}"
      }
    },
    "atlassian-jira": {
      "command": "npx",
      "args": ["-y", "mcp-remote", "https://mcp.atlassian.com/v1/mcp"]
    },
    "slack": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-slack"],
      "env": {
        "SLACK_BOT_TOKEN": "${SLACK_BOT_TOKEN}",
        "SLACK_TEAM_ID": "${SLACK_TEAM_ID}"
      }
    }
  }
}
```

## Available Skills

### `/implement-ticket` Workflow

```mermaid
sequenceDiagram
    participant Dev as Developer
    participant CC as Claude Code
    participant JIRA as JIRA MCP
    participant Git as Git
    participant GH as GitHub MCP
    participant Slack as Slack MCP

    Dev->>CC: /implement-ticket SCRUM-123
    CC->>JIRA: Fetch ticket details
    JIRA-->>CC: Summary, description, acceptance criteria

    CC->>Git: Create branch feature/SCRUM-123-description
    CC->>CC: Write tests (TDD)
    CC->>CC: Implement changes
    CC->>Git: Run tests (./mvnw verify)

    alt Tests Pass
        CC->>Git: Commit changes
        CC->>Git: Push branch
        CC->>GH: Create pull request
        GH-->>CC: PR #N created
        CC->>JIRA: Add comment with PR link
        CC->>Slack: Post notification to #pull-requests
        Slack-->>Dev: "New PR Created" notification
    else Tests Fail
        CC->>CC: Fix and retry (max 3 attempts)
        CC->>Dev: Report failure
    end
```

**Key Steps:**
1. Fetches ticket from JIRA (summary, description, acceptance criteria)
2. Creates feature branch: `feature/SCRUM-XXX-short-description`
3. Writes tests FIRST (TDD approach)
4. Implements code following project conventions
5. Runs `./mvnw verify` to validate
6. Commits with message: `feat(scope): description [SCRUM-XXX]`
7. Creates PR linking to JIRA ticket
8. Posts Slack notification to `#pull-requests`

---

### `/review-pr` Workflow

```mermaid
sequenceDiagram
    participant Dev as Developer
    participant CC as Claude Code
    participant GH as GitHub MCP
    participant Slack as Slack MCP

    Dev->>CC: /review-pr 5
    CC->>GH: Get PR details
    GH-->>CC: PR metadata, files, CI status

    CC->>CC: Verify PR is OPEN

    alt PR is Open
        CC->>CC: Categorize changed files
        CC->>CC: Analyze against pr-review-standards.md
        CC->>CC: Generate review report

        CC->>GH: Submit review with inline comments
        Note over GH: APPROVE / REQUEST_CHANGES / COMMENT

        CC->>Slack: Post review notification
        Slack-->>Dev: "PR Review Submitted" notification
    else PR is Closed/Merged
        CC->>Dev: "Only open PRs can be reviewed"
    end
```

**Review Categories:**
- **Backend (Java):** Checks for constructor injection, @ApplicationScoped, reactive patterns, OpenAPI annotations
- **Frontend (React):** Functional components, hooks, no console.log
- **Tests:** Proper naming, @QuarkusTest for integration tests
- **Security:** No hardcoded credentials, input validation

**Severity Levels:**
- :red_circle: **Critical** - Security issues, blocking bugs, data loss risks
- :yellow_circle: **Important** - Performance concerns, style violations
- :bulb: **Suggestion** - Nice-to-have improvements

---

### `/notify-pr` Workflow

```mermaid
sequenceDiagram
    participant Dev as Developer
    participant CC as Claude Code
    participant GH as GitHub MCP
    participant Slack as Slack MCP

    Dev->>CC: /notify-pr 5
    CC->>GH: Get PR details
    GH-->>CC: PR state, title, author, branches

    alt PR is Open
        CC->>Slack: Post "New PR Ready for Review"
    else PR is Merged
        CC->>Slack: Post "PR Merged!"
    else PR is Closed
        CC->>Slack: Post "PR Closed"
    end

    Slack-->>Dev: Notification in #pull-requests
```

**Message Formats:**

**Open PR:**
```
:eyes: New PR Ready for Review

#5: feat(slack): add Slack integration [SCRUM-9]
Author: username
Branch: feature/SCRUM-9-slack → main

:point_right: Review this PR
```

**Merged PR:**
```
:tada: PR Merged!

#5: feat(slack): add Slack integration [SCRUM-9]
Author: username

:rocket: Changes are now in main
```

---

## Slack Integration Flow

```mermaid
graph TB
    subgraph "Trigger Events"
        IMP[/implement-ticket<br/>creates PR]
        REV[/review-pr<br/>submits review]
        NOT[/notify-pr<br/>manual trigger]
    end

    subgraph "Slack MCP"
        BOT[MCP Bot]
    end

    subgraph "Slack Workspace"
        CH[#pull-requests<br/>Channel]
    end

    IMP -->|New PR Created| BOT
    REV -->|Review Submitted| BOT
    NOT -->|Manual Notification| BOT
    BOT --> CH
```

### Slack Bot Permissions

Required OAuth scopes:
- `chat:write` - Send messages to channels
- `channels:read` - List and find channels

### Channel Configuration

The default channel is `#pull-requests` (ID: `C0AA38NT7BL`).

To use a different channel:
1. Find the channel ID (right-click channel > Copy link)
2. Update the channel ID in skill definitions
3. Invite the bot: `/invite @MCP Bot`

---

## Data Flow Summary

| Skill | Input | MCP Servers Used | Output |
|-------|-------|------------------|--------|
| `/implement-ticket` | JIRA ticket ID | JIRA, GitHub, Slack | PR + Slack notification |
| `/review-pr` | PR number | GitHub, Slack | Review comments + Slack notification |
| `/notify-pr` | PR number | GitHub, Slack | Slack notification |

---

## Troubleshooting

### MCP Connection Issues

```bash
# Check MCP server status
/mcp

# Reconnect a specific server
/mcp  # Then select server to reconnect
```

### Common Errors

| Error | Cause | Solution |
|-------|-------|----------|
| "not_in_channel" | Bot not invited to Slack channel | `/invite @MCP Bot` in channel |
| "401 Unauthorized" | Atlassian token expired | Run `/mcp` and reconnect |
| "Can not approve your own pull request" | Reviewing own PR | Falls back to COMMENT |
