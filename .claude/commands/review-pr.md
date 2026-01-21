Review pull request $ARGUMENTS against project standards and provide actionable feedback.

## Input

`$ARGUMENTS` can be:
- PR number (e.g., `4`)
- Full GitHub PR URL (e.g., `https://github.com/owner/repo/pull/4`)

## Prerequisites

**IMPORTANT: Only review OPEN pull requests.** If the PR is closed or merged, inform the user and do not proceed with the review. Merged PRs cannot receive new reviews and reviewing them provides no actionable value.

## Steps

1. **Parse PR identifier** - Extract PR number from arguments
2. **Fetch PR details** using GitHub MCP:
   - `get_pull_request` - PR metadata, title, description, author
   - `get_pull_request_files` - List of changed files with diffs
   - `get_pull_request_status` - CI check status
3. **Verify PR is OPEN** - Check `state` field from PR metadata. If state is "closed" or "merged", stop and inform the user: "PR #{number} is {state}. Only open PRs can be reviewed."
4. **Categorize changed files**:
   - Backend: `*.java` in `rest-*`, `event-statistics`, `grpc-locations`
   - UI: Files in `ui-super-heroes/`
   - Tests: `*Test.java`, `*IT.java`, `*.test.ts`, `*.spec.ts`
   - Config: `application.properties`, `pom.xml`, `docker-compose*.yml`
   - Docs: `*.md`, `*.adoc`
5. **Read and analyze each changed file** against standards in `.claude/docs/pr-review-standards.md`
6. **Check CI status** - Note any failing checks
7. **Generate review report** in the output format below
8. **Submit review to GitHub** with inline comments on specific lines

## Review Analysis Checklist

### For Java Files
- [ ] Formatting matches `.editorconfig` (2-space indent, 180 char lines)
- [ ] Constructor injection (not field @Inject)
- [ ] @ApplicationScoped for services
- [ ] Uni<T>/Multi<T> for reactive operations
- [ ] @WithSpan for tracing on important methods
- [ ] Quarkus logging (Log.debugf/infof)
- [ ] OpenAPI annotations on REST endpoints
- [ ] No hardcoded credentials or secrets
- [ ] Input validation with @Valid, @NotNull, etc.
- [ ] No N+1 query patterns
- [ ] Pagination on list endpoints

### For React/UI Files
- [ ] Functional components with hooks
- [ ] Proper state management
- [ ] Mock API calls in tests
- [ ] No console.log statements in production code

### For Test Files
- [ ] Proper naming (*Tests.java, *IT.java)
- [ ] @QuarkusTest on integration tests
- [ ] REST Assured for endpoint tests
- [ ] Adequate coverage for new code

### For All Files
- [ ] JIRA ticket referenced in PR title/description
- [ ] No security vulnerabilities
- [ ] Documentation updated if needed

## Output Format

```markdown
# PR Review: #{PR_NUMBER} - {PR_TITLE}

## Summary
- **Author:** {author}
- **Branch:** {head} → {base}
- **Files Changed:** {count}
- **CI Status:** {passing/failing/pending}

## Changes Overview
{Brief description of what this PR does, categorized by area}

## Review Decision: {APPROVE | REQUEST_CHANGES | COMMENT}

### Critical Issues (Must Fix)
{List security issues, bugs, breaking changes - or "None found"}

### Important Issues (Should Address)
{List missing tests, performance concerns, style violations - or "None found"}

### Suggestions (Nice to Have)
{List improvements, refactoring opportunities - or "None"}

### Positive Observations
{Call out good patterns, well-written code, thorough tests}

## Test Coverage Assessment
{Analysis of test coverage for the changes}

## CI Status Details
{Details of any failing checks}
```

## Submit Review (Automatic)

After generating the report, automatically submit the review to GitHub using `create_pull_request_review` with:

**Review decision logic:**
- **REQUEST_CHANGES** - If any critical issues found (security, blocking bugs, data loss)
- **COMMENT** - If only important issues or suggestions found
- **APPROVE** - If no issues found (rare for non-trivial PRs)

**Note:** GitHub doesn't allow requesting changes on your own PR. If the reviewer is the PR author, fall back to COMMENT.

Submit with:
1. **Review body** - A summary of the review (use the Critical/Important issues sections)
2. **Inline comments** - Add comments directly on specific lines where issues were found

### Inline Comments Format

When submitting, include a `comments` array with objects containing:
- `path`: The file path relative to repo root (e.g., `rest-heroes/src/main/java/.../BadHeroService.java`)
- `line`: The line number in the file where the issue exists
- `body`: The comment text explaining the issue

**Example comments array:**
```json
[
  {
    "path": "rest-heroes/src/main/java/io/quarkus/sample/superheroes/hero/service/BadHeroService.java",
    "line": 21,
    "body": "🔴 **SECURITY**: Hardcoded credentials must never be committed to source code. Use environment variables or a secrets manager."
  },
  {
    "path": "rest-heroes/src/main/java/io/quarkus/sample/superheroes/hero/service/BadHeroService.java",
    "line": 32,
    "body": "🔴 **PERFORMANCE**: Using `.await().indefinitely()` blocks the event loop thread. Use reactive operators like `.onItem().transform()` instead."
  }
]
```

### Comment Guidelines

- **Critical issues**: Prefix with 🔴 and **SECURITY** or **CRITICAL**
- **Important issues**: Prefix with 🟡 and the category (e.g., **PERFORMANCE**, **STYLE**)
- **Suggestions**: Prefix with 💡 and **SUGGESTION**
- Keep comments concise but include WHY it's an issue
- For multi-line issues, comment on the first line
- Limit to ~10-15 most important inline comments to avoid noise

## Important Notes

- **Only review OPEN PRs** - Do not review closed or merged PRs; they cannot receive actionable feedback
- Always read the actual file content, not just the diff summary
- Reference specific line numbers when pointing out issues
- Be constructive - explain WHY something is an issue
- Acknowledge good patterns and well-written code
- If unsure about project conventions, check similar existing files
- Always submit the review automatically after analysis
