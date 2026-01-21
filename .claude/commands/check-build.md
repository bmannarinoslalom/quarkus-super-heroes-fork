Verify the build passes before creating a PR.

## Input

`$ARGUMENTS` can be:
- Empty - Verify modules with uncommitted changes
- `all` - Verify entire project
- Service name (e.g., `rest-heroes`, `rest-villains`, `rest-fights`, `rest-narration`, `event-statistics`, `grpc-locations`, `ui-super-heroes`)
- `--skip-tests` or `-st` - Compile only, skip tests
- `--fail-fast` or `-ff` - Stop on first failure (default)
- `--continue` or `-c` - Continue on failures, report all errors

## Available Modules

| Module | Path | Description |
|--------|------|-------------|
| rest-heroes | `rest-heroes/` | Hero microservice (PostgreSQL) |
| rest-villains | `rest-villains/` | Villain microservice (PostgreSQL) |
| rest-fights | `rest-fights/` | Fight orchestration (MongoDB) |
| rest-narration | `rest-narration/` | AI narration service |
| event-statistics | `event-statistics/` | Kafka statistics consumer |
| grpc-locations | `grpc-locations/` | gRPC location service (MariaDB) |
| ui-super-heroes | `ui-super-heroes/` | React frontend |

## Steps

### 1. Determine Modules to Build

**If no module specified:**
```bash
# Get list of changed files
git diff --name-only HEAD

# Also check staged files
git diff --name-only --cached

# Map files to modules
# rest-heroes/* -> rest-heroes
# rest-villains/* -> rest-villains
# etc.
```

**Module detection logic:**
- Files in `rest-heroes/` → build `rest-heroes`
- Files in `rest-villains/` → build `rest-villains`
- Files in `rest-fights/` → build `rest-fights`
- Files in `rest-narration/` → build `rest-narration`
- Files in `event-statistics/` → build `event-statistics`
- Files in `grpc-locations/` → build `grpc-locations`
- Files in `ui-super-heroes/` → build `ui-super-heroes`
- Files in root `pom.xml` or shared config → build all affected modules

### 2. Run Maven Build

**For Java modules:**
```bash
# Full verify (compile + test + package)
./mvnw verify -pl {module} -am

# Skip tests (compile only)
./mvnw verify -pl {module} -am -DskipTests

# Fail fast (default)
./mvnw verify -pl {module} -am -ff

# Continue on failure
./mvnw verify -pl {module} -am -fae
```

**For UI module:**
```bash
cd ui-super-heroes
npm ci
npm run build
npm test
```

### 3. Parse Build Output

**Compilation Errors:**
Look for patterns like:
```
[ERROR] /path/to/File.java:[line,col] error message
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin
```

Extract:
- File path
- Line number
- Column number
- Error message

**Test Failures:**
Look for patterns like:
```
[ERROR] Tests run: X, Failures: Y, Errors: Z
[ERROR]   SomeTest.testMethod:42 expected:<X> but was:<Y>
```

Parse surefire/failsafe reports:
```bash
# Find test reports
find {module}/target -name "*.xml" -path "*/surefire-reports/*"
find {module}/target -name "*.xml" -path "*/failsafe-reports/*"
```

### 4. Report Results

**Build Success Format:**
```markdown
## Build Verification: PASSED

### Modules Built
| Module | Status | Time |
|--------|--------|------|
| rest-heroes | ✅ Success | 45s |
| rest-villains | ✅ Success | 38s |

### Summary
- **Total Time:** 1m 23s
- **Tests Run:** 156
- **Tests Passed:** 156
- **Tests Skipped:** 0

✅ Ready to create PR
```

**Build Failure Format:**
```markdown
## Build Verification: FAILED

### Modules Built
| Module | Status | Time |
|--------|--------|------|
| rest-heroes | ❌ Failed | 23s |
| rest-villains | ⏭️ Skipped | - |

### Compilation Errors

#### rest-heroes
| File | Line | Error |
|------|------|-------|
| `HeroService.java` | 45 | cannot find symbol: method getFoo() |
| `HeroResource.java` | 23 | incompatible types |

### Test Failures

#### rest-heroes
| Test | Method | Error |
|------|--------|-------|
| `HeroServiceTest` | `testFindRandom` | expected:<Hero> but was:<null> |

### How to Fix
1. Open the files listed above
2. Fix the compilation/test errors
3. Run `/check-build` again

❌ Fix errors before creating PR
```

## Error Handling

### Maven Not Found
```
Maven is not available.

Please ensure Maven wrapper exists:
  ls -la ./mvnw

Or install Maven:
  brew install maven  # macOS
  sudo apt install maven  # Ubuntu
```

### Build Timeout
```
Build timed out after 10 minutes.

This may indicate:
- Very slow tests
- Infinite loop in code
- Resource contention

Try:
- Run with --skip-tests to isolate compilation issues
- Check for runaway processes
- Increase timeout if needed
```

### Out of Memory
```
Build failed with OutOfMemoryError.

Try increasing Maven memory:
  export MAVEN_OPTS="-Xmx2g"

Or add to .mvn/jvm.config:
  -Xmx2g
```

## Examples

### Verify changed modules
```
> /check-build

Detecting changed modules...
Found changes in: rest-heroes, rest-fights

Building rest-heroes...
Building rest-fights...

## Build Verification: PASSED

### Modules Built
| Module | Status | Time |
|--------|--------|------|
| rest-heroes | ✅ Success | 45s |
| rest-fights | ✅ Success | 52s |

### Summary
- **Total Time:** 1m 37s
- **Tests Run:** 89
- **Tests Passed:** 89

✅ Ready to create PR
```

### Verify specific module
```
> /check-build rest-heroes

Building rest-heroes...

## Build Verification: PASSED

### Modules Built
| Module | Status | Time |
|--------|--------|------|
| rest-heroes | ✅ Success | 45s |

### Summary
- **Total Time:** 45s
- **Tests Run:** 42
- **Tests Passed:** 42

✅ Ready to create PR
```

### Compile only (skip tests)
```
> /check-build --skip-tests

Detecting changed modules...
Found changes in: rest-heroes

Compiling rest-heroes (tests skipped)...

## Build Verification: PASSED

### Modules Built
| Module | Status | Time |
|--------|--------|------|
| rest-heroes | ✅ Compiled | 12s |

### Summary
- **Total Time:** 12s
- **Tests:** Skipped

⚠️ Tests were skipped. Run full verification before PR.
```

### Build with failures
```
> /check-build rest-heroes

Building rest-heroes...

## Build Verification: FAILED

### Modules Built
| Module | Status | Time |
|--------|--------|------|
| rest-heroes | ❌ Failed | 23s |

### Compilation Errors

#### rest-heroes/src/main/java/io/quarkus/sample/superheroes/hero/service/HeroService.java
| Line | Error |
|------|-------|
| 45 | cannot find symbol: method findRandom() |
| 67 | method does not override or implement a method from a supertype |

### How to Fix
1. Check `HeroService.java:45` - method `findRandom()` doesn't exist
2. Check `HeroService.java:67` - `@Override` annotation on non-overriding method

❌ Fix errors before creating PR
```

## Important Notes

- Default behavior is fail-fast (`-ff`) to save time
- Use `--continue` flag to see all errors at once
- Build verification should pass before creating any PR
- The `--skip-tests` flag is useful for quick compilation checks but should not be used as final verification
- For UI module, both build and test are run unless `--skip-tests` is specified
- Build times vary based on machine performance and test complexity
