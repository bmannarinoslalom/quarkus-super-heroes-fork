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
6. **Run all tests**: `./mvnw verify` in affected service(s)
7. **If tests pass**:
   - Commit with message: `feat(scope): description [$ARGUMENTS]` (or `fix(scope):` for bugs)
   - Push branch
   - Create PR linking to the ticket
   - Add comment to JIRA with the PR link
   - **Post Slack notification** to `#pull-requests` channel (see below)
8. **If tests fail**: Fix and retry (max 3 attempts), then report

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