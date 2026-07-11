# Prompt: Write AssertX Cucumber tests

Copy and paste the block below into Cursor, Claude Code, or another coding agent when adding or extending AssertX feature files and step definitions.

---

```
Write AssertX API integration tests for this service using Cucumber feature files and AssertX built-in / custom step definitions.

## Context
- AssertX is already (or will be) integrated: ITMain extends com.olx.assertx.ITRunner.
- Features live under src/test/resources (or the path configured in @CucumberOptions).
- Glue must include com.olx.assertx.stepdefinitions plus any custom step-definition package.
- Mocks and ports are configured in src/test/resources/api-testing.yml; app clients use ${mock-name} placeholders.

## Built-in steps (prefer these)
- Given I have <host> host
  Example: Given I have http://0.0.0.0:9000/ host
- Given I have <path> API
  Example: Given I have /api/v1/orders API
- Given I have following query parameters
  | key | value |
- Given I have following headers
  | Content-Type     | application/json |
  | X-Default-Tenant | default          |
- Given I have a request body in <contextKey>
  (payload must already be stored in CucumberTestContext under that key via a custom Given step)
- When Execute GET|POST|PUT|PATCH|DELETE request using REST
- Then Validate status code is: <code>

## Custom steps — only when needed
- Extend BaseRestStepDefinition (or BaseStepDefinition) for domain-specific setup/assertions.
- Put request JSON into the test context before `I have a request body in <key>`.
- Use getJedisClient() / getToxiProxyClient() from BaseStepDefinition when validating Redis or injecting latency/failures.
- For downstream HTTP mocks: add Express JS mocks under the path in api-testing.yml mocks.externalServices.userSpecified and map endpoints (methodType, path, method).
- Do not duplicate built-in step wording; reuse AssertX phrases exactly so Cucumber matches.

## Feature file style
Feature: <capability>

  Scenario: <happy path>
    Given I have http://0.0.0.0:<appPort>/ host
    And I have /api/... API
    And I have following headers
      | Content-Type | application/json |
    When Execute GET request using REST
    Then Validate status code is: 200

  Scenario Outline: <variants>
    ...
    Examples:
      | id | status |
      | 1  | 200    |

## Rules
1. Cover happy path + important 4xx/5xx cases; keep scenarios readable for non-developers.
2. Align host/port with application-integration-test server.port and api-testing.yml.
3. If the scenario needs Redis/MySQL/Kafka/etc., ensure the matching mock is enabled in api-testing.yml (do not assume mocks exist).
4. For external downstream calls, add or update userSpecified mock routes and JS handlers; do not hit real third-party services.
5. After adding features, note how to run: ./mvnw clean integration-test -DskipUnitTests=true (Docker required).
6. Minimal diff — no unrelated refactors.

## Task
Inspect the service APIs (controllers/OpenAPI) and existing features. Add or update .feature files and only the custom step definitions / mocks required for the scenarios below:

<DESCRIBE THE ENDPOINTS OR BEHAVIOUR TO TEST>
```

---

## When to use

- Adding BDD scenarios for new or changed REST endpoints
- Extending coverage with negative paths, headers, or query params
- Creating custom step definitions for request bodies, Redis checks, or Toxiproxy faults
- Wiring Express-based downstream mocks for userSpecified external services

## Tips for better results

- Paste or `@`-mention a sample existing `.feature` and `api-testing.yml` from the target repo
- Name the exact endpoints, expected status codes, and whether mocks (Redis, MySQL, etc.) are already enabled
- For SOAP/XML downstreams, also follow the AssertX README section on SOAP API Consumer support
