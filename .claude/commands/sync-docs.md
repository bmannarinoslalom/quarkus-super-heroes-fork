Update Confluence documentation to reflect recent changes in the codebase.

## When to Use

Run this skill after:
- Major code changes (new services, renamed components)
- Architecture changes (new databases, messaging patterns, protocols)
- Claude Code configuration changes (new MCP servers, new skills)
- Deployment configuration changes (new K8s configs, Docker Compose updates)

## Arguments

`$ARGUMENTS` can be:
- `all` - Review and update all documentation
- `architecture` - Update architecture diagrams and service details
- `claude` - Update Claude Code integration docs
- `deployment` - Update deployment architecture docs
- (empty) - Auto-detect what changed based on recent commits

## Steps

1. **Detect changes** (if no argument provided):
   - Check `git log --oneline -20` for recent commits
   - Check `git diff main...HEAD --stat` for changed files
   - Identify which documentation sections are affected:
     - Changes to `rest-*`, `grpc-*`, `event-*`, `ui-*` → Architecture docs
     - Changes to `.claude/`, `CLAUDE.md`, `.mcp.json` → Claude Code docs
     - Changes to `deploy/` → Deployment docs

2. **Regenerate diagrams** if architecture changed:
   - Use `mcp__mermaid__generate` to create updated PNGs
   - Save to `.claude/diagrams/`
   - Diagrams to update:
     - `system-architecture.png` - If services added/removed/renamed
     - `communication-patterns.png` - If protocols or data flows changed
     - `docker-compose-architecture.png` - If Docker config changed
     - `claude-code-mcp-architecture.png` - If MCP servers changed
     - `observability-stack.png` - If monitoring config changed

3. **Update Confluence pages** using Atlassian MCP:
   - Fetch current page content with `getConfluencePage`
   - Modify content to reflect changes
   - Update with `updateConfluencePage`

4. **Confluence Pages to Update**:

   | Page ID | Title | Update When |
   |---------|-------|-------------|
   | 688129 | System Architecture | Services change |
   | 720897 | Service Details | Service tech stack changes |
   | 753665 | Communication Patterns | Protocols/messaging changes |
   | 786433 | Deployment Architecture | Docker/K8s config changes |
   | 851969 | Claude Code Integration | MCP/skills changes |
   | 884737 | Getting Started | Prerequisites/setup changes |
   | 950273 | Operations | Monitoring config changes |

5. **Commit and push** diagram changes:
   ```bash
   git add .claude/diagrams/*.png
   git commit -m "docs(diagrams): update architecture diagrams [SCRUM-XXX]"
   git push personal <branch>
   ```

6. **Report** what was updated

## Confluence Space Details

- **Space**: Software Development (SD)
- **Cloud ID**: `f0864feb-75a5-46e1-bdbc-6f7514f4ae43`
- **Base URL**: https://mcpdemo.atlassian.net/wiki

## Diagram GitHub URLs

After pushing diagrams, they're accessible at:
```
https://raw.githubusercontent.com/bmannarinoslalom/quarkus-super-heroes-fork/<branch>/.claude/diagrams/<filename>.png
```

## Example Usage

```
/sync-docs                    # Auto-detect and update affected docs
/sync-docs all                # Update all documentation
/sync-docs architecture       # Update only architecture docs
/sync-docs claude             # Update only Claude Code docs
```

## Important Notes

- Always verify changes before updating Confluence
- Keep diagram Mermaid source in sync with generated PNGs
- Reference JIRA tickets in commit messages when applicable
- Notify team in Slack if major documentation changes were made
