# AssertX

**End-to-end API tests for Java services, in plain English, with real dependencies in Docker.**

AssertX boots your Spring Boot or Dropwizard service, starts the Redis, MySQL, Kafka, LocalStack,
Postgres, Solr, OpenSearch and HTTP mocks it needs, and runs your Cucumber scenarios against it —
from a single YAML file.

```gherkin
Scenario: Greet a known user
  Given I have http://0.0.0.0:9000/ host
  And I have /api/v1/greetings/42 API
  When Execute GET request using REST
  Then Validate status code is: 200
```

```yaml
# src/test/resources/api-testing.yml
mainClass: com.example.greeting.GreetingApplication
application:
  spring:
    profile: integration-test
mocks:
  redis:
    enabled: true
  kafka:
    enabled: true
    topics: [orderCreated]
```

## Why AssertX

- **One YAML, whole environment** — databases, queues, AWS (LocalStack), search and HTTP mocks.
- **Readable tests** — Gherkin scenarios QA and product can review.
- **Chaos built in** — Toxiproxy in front of every mock for latency and failure tests.
- **Coverage you can gate on** — JaCoCo integration-test coverage with a minimum threshold.
- **SOAP too** — XML downstream mocks for legacy integrations.
- **AI-ready** — [prompts](ai-prompts.md) that let coding agents integrate AssertX and write tests.

## Next steps

- [Run the demo in 5 minutes](https://github.com/olx-india/AssertX/tree/main/samples/spring-boot-demo)
- [Add AssertX to your service](getting-started.md)
- [Built-in steps](steps.md)
- [How it compares](comparison.md)
