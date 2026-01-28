Interactively create a well-structured JIRA story in the SCRUM project with enough implementation detail for `/implement-ticket` to pick up.

$ARGUMENTS is an optional short title or summary for the story.

## Steps

1. **Gather title**: If `$ARGUMENTS` is provided and non-empty, use it as the story title. Otherwise, ask the user to provide a short title/summary for the story.

2. **Gather feature description**: Ask the user to describe the feature — what it should do, user-facing behavior, and motivation. Use `AskUserQuestion` or direct prompting.

3. **Identify affected modules**: Ask the user which services/modules are affected. Offer these choices (allow multiple):
   - `rest-heroes` — Hero microservice (Quarkus + PostgreSQL)
   - `rest-villains` — Villain microservice (Quarkus + PostgreSQL)
   - `rest-fights` — Fight orchestration service (Quarkus + MongoDB)
   - `rest-narration` — AI narration service (Quarkus + LangChain4j)
   - `grpc-locations` — gRPC location service (Quarkus + MariaDB)
   - `event-statistics` — Kafka consumer for fight statistics
   - `ui-super-heroes` — React frontend

4. **Explore the codebase**: For each affected module, explore the relevant source code to understand:
   - Existing patterns, key classes, and interfaces
   - REST endpoints, entity models, and service layers
   - Test patterns and conventions used
   - Configuration and dependency details
   Use this information to write concrete technical guidance in the story.

5. **Draft the story**: Compose a well-structured JIRA story description using this template:

```markdown
## Description
{User-facing description of the feature and motivation}

## Acceptance Criteria
- [ ] {Criterion 1}
- [ ] {Criterion 2}
- ...

## Technical Approach
### Files to Modify
- `path/to/file.java` — {what changes}
- ...

### Implementation Notes
- {Key patterns, conventions, or architectural decisions}
- {Dependencies or prerequisites}

## Testing Requirements
- Unit tests: {what to test}
- Integration tests: {what to test}
- Manual verification: {how to verify}
```

   Ensure:
   - Acceptance criteria are specific and testable
   - Technical approach references actual files and classes found during codebase exploration
   - Implementation notes reference project conventions (constructor injection, `@ApplicationScoped`, Mutiny reactive types, OpenAPI annotations, etc.)
   - Testing requirements follow project patterns (`*Tests.java` for unit, `*IT.java` for integration with `@QuarkusTest`)

6. **Review with user**: Present the full draft (title + description) to the user. Ask if they want to make any changes before creating the ticket. Incorporate feedback if given.

7. **Create the JIRA story**: Use `createJiraIssue` with:
   - **cloudId**: Use `getAccessibleAtlassianResources` to get the cloud ID
   - **projectKey**: `SCRUM`
   - **issueTypeName**: `Story`
   - **summary**: The story title
   - **description**: The full formatted description from step 5

8. **Report the result**: Output the created ticket key (e.g., `SCRUM-XXX`) and a direct link to the ticket. Mention that the user can implement it with `/implement-ticket SCRUM-XXX`.

## Guidelines

- Keep acceptance criteria to 3–7 items — specific enough to verify, broad enough to allow implementation flexibility.
- Reference real file paths and class names discovered during codebase exploration, not hypothetical ones.
- Follow the project's conventions documented in CLAUDE.md (indentation, DI style, reactive patterns, test naming, etc.).
- If the feature spans multiple modules, organize the Technical Approach section by module.
- The story should contain enough detail that `/implement-ticket` can execute without ambiguity, but not so much that it prescribes every line of code.
