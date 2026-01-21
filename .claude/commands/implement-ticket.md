Read JIRA ticket $ARGUMENTS and implement it end-to-end.

## Steps

1. **Fetch ticket** from JIRA - extract summary, description, acceptance criteria
2. **Create branch**: `feature/$ARGUMENTS-{short-description}` (or `bugfix/` for bugs)
3. **Analyze the change** - understand what code/config needs to change
4. **Write tests FIRST** (TDD approach):
   - Unit tests for new/changed logic
   - Integration tests to verify the fix works end-to-end
   - Tests should fail initially (proving they test the right thing)
5. **Implement** the code/config changes following project conventions
6. **Update documentation** (see Documentation Requirements below):
   - Update README.md if adding new features, commands, or changing setup
   - Update relevant Confluence pages if architecture or workflows change
   - Update CLAUDE.md if adding new skills or changing conventions
7. **Run all tests**: `./mvnw verify` in affected service(s)
8. **If tests pass**:
   - Commit with message: `feat(scope): description [$ARGUMENTS]` (or `fix(scope):` for bugs)
   - Push branch
   - Create PR linking to the ticket
   - Add comment to JIRA with the PR link
   - **Post Slack notification** to `#pull-requests` channel (see below)
9. **If tests fail**: Fix and retry (max 3 attempts), then report

## Testing Requirements

**All changes MUST include tests that verify the fix:**

- **Code changes**: Unit tests + integration tests
- **Configuration changes** (application.properties, docker-compose, etc.):
  - Integration test that loads the config and verifies behavior
  - Or a test that validates the configuration is correctly applied
- **No change is complete without a test proving it works**

## Test Patterns for This Project

- Unit tests: `src/test/java/.../SomethingTest.java`
- Integration tests: `src/test/java/.../SomethingIT.java` (uses `@QuarkusTest`)
- Config validation tests: Use `@ConfigProperty` injection and assert expected values
- REST endpoint tests: Use REST Assured with `@QuarkusTest`

## Documentation Requirements

**All changes MUST include documentation updates where applicable:**

### README.md Updates
Update the repository README when:
- Adding new features or capabilities
- Changing setup/installation instructions
- Adding new environment variables or configuration
- Changing build or run commands
- Adding new services or dependencies

### Confluence Updates
Update relevant Confluence pages when:
- **Architecture changes**: New services, databases, messaging patterns → Update [Architecture](https://mcpdemo.atlassian.net/wiki/spaces/SD/pages/655361/Architecture) page
- **Claude Code changes**: New skills, MCP servers, workflows → Update [Claude Code Integration](https://mcpdemo.atlassian.net/wiki/spaces/SD/pages/851969/Claude+Code+Integration) page
- **Deployment changes**: Docker/K8s config, new environments → Update deployment docs
- **API changes**: New endpoints, changed contracts → Update API documentation

### CLAUDE.md Updates
Update CLAUDE.md when:
- Adding new Claude Code skills (slash commands)
- Changing project conventions or coding standards
- Adding new MCP server integrations
- Changing the project structure

### Documentation Checklist
Before creating a PR, verify:
- [ ] README.md is current with any new features/setup changes
- [ ] Relevant Confluence pages reflect the changes
- [ ] CLAUDE.md is updated if Claude Code behavior changes
- [ ] Code comments are added for complex logic
- [ ] OpenAPI annotations are complete for REST endpoints

## Slack Notification

After creating the PR, post a notification to Slack `#pull-requests` channel (ID: `C0AA38NT7BL`) using `slack_post_message`:

```
:rocket: *New PR Created*

*<{PR_URL}|#{PR_NUMBER}: {PR_TITLE}>*
Author: {author}
Branch: `{head}` → `{base}`
JIRA: <{JIRA_URL}|{TICKET_ID}>

{Brief description of changes}

:point_right: <{PR_URL}|Review this PR>
```

Always ask for confirmation before pushing or creating the PR.