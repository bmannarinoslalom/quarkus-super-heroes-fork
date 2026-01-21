Run tests for the Quarkus Super Heroes project and report results.

## Input

`$ARGUMENTS` can be:
- Empty - Run tests for modules with uncommitted changes
- Service name (e.g., `rest-heroes`, `rest-villains`, `rest-fights`, `rest-narration`, `grpc-locations`, `event-statistics`)
- `all` - Run tests for all modules
- Service name with flags (e.g., `rest-heroes -it` for integration tests only)

## Flags

- `-it` or `--integration` - Run only integration tests (`*IT.java`)
- `-ut` or `--unit` - Run only unit tests (`*Test.java`, excluding `*IT.java`)
- `-v` or `--verbose` - Show full Maven output
- `--skip-build` - Skip compilation, run tests only

## Steps

1. **Parse arguments** - Determine which modules to test and any flags
2. **Detect changed modules** (if no service specified):
   - Run `git diff --name-only HEAD` to find changed files
   - Map changed files to their parent modules
3. **Validate module names** - Ensure specified modules exist
4. **Run tests** using Maven:
   - Single module: `./mvnw test -pl {module}`
   - All modules: `./mvnw test`
   - Integration tests: `./mvnw verify -pl {module} -DskipUnitTests`
   - Unit tests only: `./mvnw test -pl {module} -DskipITs`
5. **Parse test results** from surefire/failsafe reports
6. **Display summary** with pass/fail counts
7. **Show failed test details** if any failures

## Module Mapping

| Module Name | Directory | Tests Location |
|-------------|-----------|----------------|
| `rest-heroes` | `rest-heroes/` | `rest-heroes/src/test/java/` |
| `rest-villains` | `rest-villains/` | `rest-villains/src/test/java/` |
| `rest-fights` | `rest-fights/` | `rest-fights/src/test/java/` |
| `rest-narration` | `rest-narration/` | `rest-narration/src/test/java/` |
| `grpc-locations` | `grpc-locations/` | `grpc-locations/src/test/java/` |
| `event-statistics` | `event-statistics/` | `event-statistics/src/test/java/` |
| `ui-super-heroes` | `ui-super-heroes/` | Uses `npm test` |

## Maven Commands

```bash
# Unit tests for specific module
./mvnw test -pl rest-heroes

# Integration tests for specific module
./mvnw verify -pl rest-heroes -DskipUnitTests

# All tests for specific module
./mvnw verify -pl rest-heroes

# All tests for all modules
./mvnw verify

# Multiple modules
./mvnw test -pl rest-heroes,rest-villains
```

## UI Tests (Special Case)

For `ui-super-heroes`, use npm instead of Maven:

```bash
cd ui-super-heroes && npm test
```

## Output Format

```
## Test Results: {module(s)}

### Summary
| Metric | Count |
|--------|-------|
| Tests Run | {total} |
| Passed | {passed} |
| Failed | {failed} |
| Skipped | {skipped} |
| Time | {duration} |

### Status: {PASSED | FAILED}

{If PASSED}
All tests passed successfully.

{If FAILED}
### Failed Tests

#### {TestClassName}

**{testMethodName}**
- File: `{path/to/TestClass.java}:{line}`
- Error: {error message}
- Type: {exception type}

{Stack trace snippet - first 5 lines}

---

### Next Steps
{If failed: Suggestions for fixing}
{If passed: Ready for commit/PR}
```

## Parsing Test Results

Test results are found in:
- **Surefire** (unit tests): `{module}/target/surefire-reports/TEST-*.xml`
- **Failsafe** (integration tests): `{module}/target/failsafe-reports/TEST-*.xml`

Parse XML files to extract:
- `<testsuite tests="X" failures="Y" errors="Z" skipped="W">`
- `<testcase name="methodName" classname="ClassName">`
- `<failure message="..." type="...">`

## Error Handling

- **Module not found**: "Module '{name}' not found. Available modules: rest-heroes, rest-villains, rest-fights, rest-narration, grpc-locations, event-statistics, ui-super-heroes"
- **No changes detected**: "No uncommitted changes detected. Specify a module or use 'all' to run all tests."
- **Build failure**: Show compilation errors before test results
- **Timeout**: Tests timeout after 10 minutes by default

## Examples

### Run tests for changed modules
```
> /run-tests

Detecting changed files...
Found changes in: rest-heroes/

Running tests for: rest-heroes
./mvnw test -pl rest-heroes

## Test Results: rest-heroes

### Summary
| Metric | Count |
|--------|-------|
| Tests Run | 24 |
| Passed | 24 |
| Failed | 0 |
| Skipped | 0 |
| Time | 8.3s |

### Status: PASSED

All tests passed successfully.
```

### Run all integration tests
```
> /run-tests all -it

Running integration tests for all modules...
./mvnw verify -DskipUnitTests

## Test Results: all modules (integration)

### Summary
| Metric | Count |
|--------|-------|
| Tests Run | 45 |
| Passed | 43 |
| Failed | 2 |
| Skipped | 0 |
| Time | 2m 34s |

### Status: FAILED

### Failed Tests

#### FightResourceIT

**shouldGetRandomFighters**
- File: `rest-fights/src/test/java/io/quarkus/sample/superheroes/fight/rest/FightResourceIT.java:89`
- Error: Expected status 200 but got 500
- Type: AssertionError

```
java.lang.AssertionError: Expected status 200 but got 500
    at io.restassured.internal.ValidatableResponseImpl.statusCode
    at io.quarkus.sample.superheroes.fight.rest.FightResourceIT.shouldGetRandomFighters
```

---

### Next Steps
1. Check if dependent services (rest-heroes, rest-villains) are configured correctly
2. Review the test at line 89 for assertion logic
3. Check application logs for the 500 error details
```

### Run unit tests for specific module
```
> /run-tests rest-villains -ut

Running unit tests for: rest-villains
./mvnw test -pl rest-villains -DskipITs

## Test Results: rest-villains (unit)

### Summary
| Metric | Count |
|--------|-------|
| Tests Run | 12 |
| Passed | 12 |
| Failed | 0 |
| Skipped | 0 |
| Time | 3.1s |

### Status: PASSED

All tests passed successfully.
```

## Important Notes

- Always show the Maven command being executed
- For long-running tests, provide periodic status updates
- If tests are taking too long, suggest using `-ut` for faster feedback
- After successful tests, remind about next steps (commit, PR)
- Integration tests require infrastructure (databases, Kafka) - warn if not running
