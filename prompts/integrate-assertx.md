# Prompt: Integrate AssertX into a Java service

Copy and paste the block below into Cursor, Claude Code, or another coding agent when wiring AssertX into a Maven or Gradle service.

---

```
Integrate AssertX (https://github.com/olx-india/AssertX) into this service for Cucumber-based API integration tests.

## Goals
1. Add AssertX as a test dependency and wire Maven Failsafe (or Gradle integrationTest) so ITs run separately from unit tests.
2. Create ITMain extending com.olx.assertx.ITRunner with @CucumberOptions for features and glue.
3. Add src/test/resources/api-testing.yml for the app main class, Spring/Dropwizard profile, and only the mocks this service needs.
4. Add application-integration-test config (or Dropwizard config) using ${mock-name} port placeholders AssertX fills at runtime.
5. Add .gitignore entry for /it (AssertX runtime output).
6. Prefer existing AssertX built-in steps; only add custom step definitions when needed.

## Constraints
- Docker must be available locally for ITs.
- Glue packages must include both com.olx.assertx.stepdefinitions and any custom step-definition package.
- Exclude ITMain from Surefire; include it in Failsafe.
- Enable only mocks the service actually uses (redis, mysql, postgresql, kafka+zookeeper, localstack, opensearch, solr, toxiproxy, externalServices).
- Do not invent private Maven coordinates; use in.olx:assertx with the version from the AssertX README / release.
- Keep the diff minimal; do not refactor production code unless required for the integration-test profile.

## Built-in Cucumber steps (reuse these)
- Given I have <host> host
- Given I have <path> API
- Given I have following query parameters (DataTable)
- Given I have following headers (DataTable)
- Given I have a request body in <contextKey>
- Given I have following multipart file specifications (DataTable)
- When Execute <GET|POST|PUT|PATCH|DELETE> request using REST
- Then Validate status code is: <int>

## Deliverables
- pom.xml / build.gradle.kts changes
- ITMain + optional custom step defs
- api-testing.yml + application IT config
- At least one smoke .feature that hits a health or known endpoint
- Brief notes on how to run: ./mvnw clean integration-test -DskipUnitTests=true (Docker required)

Read the AssertX README Integration Steps and match this repo’s existing package layout and framework (Spring or Dropwizard). Ask before enabling mocks that need warm-up scripts (SQL, LocalStack shell, Solr collections) if those assets are missing.
```

---

## When to use

- Bootstrapping AssertX in a new or existing Java microservice
- Migrating from ad-hoc RestAssured suites to AssertX + Cucumber
- Adding Docker-backed dependency mocks (Redis, MySQL, Kafka, etc.) for ITs
