# Git Workflow Standards

## Branch Naming
- Feature: `feature/SCRUM-{number}-short-description`
- Bug: `bugfix/SCRUM-{number}-short-description`
- Hotfix: `hotfix/SCRUM-{number}-short-description`

## Commit Messages
```
SCRUM-{number}: Brief description

Detailed explanation if needed
- Bullet points for multiple changes
- Reference related tickets

Testing:
- Unit tests added/updated
- Integration tests added/updated
```

## Testing Requirements
- All code changes require tests
- Unit tests: `@QuarkusTest` with descriptive names
- Integration tests: Suffix with `IT`, use `@QuarkusIntegrationTest`
- Test method naming: `testMethodName_SCRUM{number}` for traceability
- Minimum coverage: 80%

## Pull Request Process
1. Create branch from `main` following naming convention
2. Make changes and commit with SCRUM number
3. Add/update tests with SCRUM number in test names
4. Push to personal fork or origin
5. Create PR with:
   - Title: `SCRUM-{number}: Description`
   - Link to Jira ticket
   - Description of changes
   - Testing performed (unit + integration)
   - Test coverage report

## Code Review
- All PRs require review before merge
- Address review comments
- Squash commits if needed
- Ensure tests pass

## CI/CD
- Automated tests run on PR
- Docker images built on merge to main
- Kubernetes manifests auto-generated
