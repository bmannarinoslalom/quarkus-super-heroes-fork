# PR Review Standards

This document defines the review standards and criteria for the Quarkus Super Heroes project. Use these standards when reviewing pull requests to ensure consistency, quality, and maintainability.

---

## Code Standards (Java/Quarkus Backend)

### Formatting & Style

- **Indentation:** 2 spaces (per `.editorconfig`)
- **Line length:** Maximum 180 characters
- **Line endings:** LF (Unix-style)
- **Trailing whitespace:** Not allowed
- **Final newline:** Required

### Dependency Injection

- **Constructor injection preferred** over field injection
  ```java
  // Good
  @ApplicationScoped
  public class HeroService {
      private final HeroRepository repository;

      public HeroService(HeroRepository repository) {
          this.repository = repository;
      }
  }

  // Avoid
  @ApplicationScoped
  public class HeroService {
      @Inject
      HeroRepository repository;  // Field injection
  }
  ```

### Bean Scopes

- Use `@ApplicationScoped` for services and repositories
- Use `@RequestScoped` only when request-specific state is needed
- Avoid `@Singleton` unless specifically required

### Reactive Programming (Mutiny)

- Use `Uni<T>` for single-value async operations
- Use `Multi<T>` for streams/collections
- **Never block reactive chains** - avoid `.await().indefinitely()` in production code
- Chain operations properly with `.onItem()`, `.onFailure()`, etc.

### Observability

- Add `@WithSpan` annotation for tracing important operations
- Use Quarkus logging: `Log.debugf()`, `Log.infof()`, `Log.warnf()`, `Log.errorf()`
- Include correlation context in log messages where appropriate

### REST Endpoints

- Use JAX-RS annotations (`@GET`, `@POST`, `@PUT`, `@DELETE`)
- Include OpenAPI annotations (`@Operation`, `@APIResponse`, `@Tag`)
- Return appropriate HTTP status codes
- Use `@Valid` for request body validation

---

## Code Standards (React UI)

### Component Structure

- **Functional components with hooks** (no class components)
- Keep components focused and single-responsibility
- Extract reusable logic into custom hooks

### State Management

- Use React hooks (`useState`, `useEffect`, `useContext`)
- Lift state appropriately - not too high, not too low

### Testing

- Use `@testing-library/react` for component tests
- **Mock API calls in tests** - never make real network requests
- Test user interactions, not implementation details
- Prefer `getByRole`, `getByLabelText` over `getByTestId`

### Styling

- Follow existing patterns in the codebase
- Use consistent naming conventions

---

## Test Coverage Requirements

### Unit Tests

- **Naming:** `*Tests.java` (e.g., `HeroServiceTests.java`)
- Test business logic in isolation
- Mock external dependencies
- Cover edge cases and error conditions

### Integration Tests

- **Naming:** `*IT.java` (e.g., `HeroResourceIT.java`)
- Annotate with `@QuarkusTest`
- Test actual component interactions
- Use `@TestProfile` for test-specific configurations

### REST Endpoint Tests

- Use **REST Assured** for HTTP testing
- Test all HTTP methods and status codes
- Validate response bodies and headers
- Test error scenarios (400, 404, 500)

### Contract Tests

- Use **Pact** for consumer-driven contracts
- Define contracts between services
- Verify provider compliance

### UI Tests

- Use **Jest** with React Testing Library
- Test component rendering
- Test user interactions
- Mock backend responses

### Coverage Expectations

- New code should include corresponding tests
- Bug fixes should include regression tests
- Critical paths must have integration tests

---

## Security Checklist

### Credentials & Secrets

- [ ] **No hardcoded credentials** in code
- [ ] Secrets managed via environment variables or config
- [ ] No API keys, passwords, or tokens in commits

### Input Validation

- [ ] Use `@Valid` on request bodies
- [ ] Use `@NotNull`, `@NotBlank`, `@Size`, etc. for field validation
- [ ] Validate path parameters and query parameters
- [ ] Sanitize user input before processing

### SQL/NoSQL Injection

- [ ] Use parameterized queries (Panache handles this)
- [ ] No string concatenation for queries
- [ ] Validate IDs and lookup parameters

### Dependency Security

- [ ] No known vulnerable dependencies
- [ ] Dependencies from trusted sources
- [ ] Minimal dependency footprint

### API Security

- [ ] Appropriate authentication/authorization checks
- [ ] Rate limiting considerations for public endpoints
- [ ] No sensitive data in logs or error messages

---

## Performance Checks

### Database Queries

- [ ] **No N+1 query patterns** - use eager fetching or batch queries
- [ ] Appropriate indexes for query patterns
- [ ] Use projections when full entities aren't needed

### Pagination

- [ ] **List endpoints must support pagination**
- [ ] Use `@QueryParam` for `page` and `size`
- [ ] Return total count for pagination metadata
- [ ] Set reasonable default and maximum page sizes

### Reactive Chains

- [ ] **No blocking operations in reactive chains**
- [ ] Use `.runSubscriptionOn()` for blocking operations if unavoidable
- [ ] Proper error handling in chains
- [ ] Consider timeouts for external calls

### Caching

- [ ] Appropriate caching for frequently accessed data
- [ ] Cache invalidation strategy defined
- [ ] Consider cache warming for critical data

---

## Documentation Requirements

### Javadoc

- [ ] **Public methods must have Javadoc**
- [ ] Document parameters, return values, and exceptions
- [ ] Include usage examples for complex methods

### OpenAPI Annotations

- [ ] `@Operation` with summary and description
- [ ] `@APIResponse` for each response code
- [ ] `@Tag` for endpoint grouping
- [ ] Request/response schemas documented

### JIRA Ticket Reference

- [ ] **Commit messages reference JIRA ticket** (e.g., `SCRUM-123`)
- [ ] PR title or description includes ticket reference
- [ ] Link to ticket for context

### README Updates

- [ ] Update README if adding new features
- [ ] Document configuration changes
- [ ] Include any new setup requirements

---

## Review Decision Criteria

### APPROVE
- All critical items pass
- Code is well-tested
- No security concerns
- Minor suggestions only

### REQUEST_CHANGES
- Security vulnerabilities found
- Breaking changes without migration path
- Missing required tests
- Critical bugs identified
- Blocking issues present

### COMMENT
- Non-blocking suggestions
- Questions need answers before decision
- Need clarification on approach

---

## Severity Levels

### Critical (Must Fix)
- Security vulnerabilities
- Data loss potential
- Breaking production functionality
- Missing required tests for critical paths

### Important (Should Address)
- Performance issues
- Missing tests for new code
- Documentation gaps
- Code style violations

### Suggestion (Nice to Have)
- Code organization improvements
- Additional test cases
- Documentation enhancements
- Refactoring opportunities