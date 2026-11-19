<p align="center">
  <img src="docs/assets/logo.svg" alt="AssertX" width="360">
</p>

<p align="center">
  <b>End-to-end API tests for Java services — in plain English, with real dependencies in Docker.</b>
</p>

<p align="center">
  <a href="https://central.sonatype.com/artifact/in.olx/assertx"><img src="https://img.shields.io/maven-central/v/in.olx/assertx?label=Maven%20Central" alt="Maven Central"></a>
  <a href="https://github.com/olx-india/AssertX/actions/workflows/package-verify.yml"><img src="https://github.com/olx-india/AssertX/actions/workflows/package-verify.yml/badge.svg" alt="CI"></a>
  <a href="https://hub.docker.com/r/olxindia/assertx"><img src="https://img.shields.io/docker/pulls/olxindia/assertx?label=Docker%20pulls" alt="Docker pulls"></a>
  <img src="https://img.shields.io/badge/java-17%2B-blue.svg" alt="Java 17+">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-Apache%202.0-blue.svg" alt="License"></a>
  <a href="https://github.com/olx-india/AssertX/stargazers"><img src="https://img.shields.io/github/stars/olx-india/AssertX?style=social" alt="Stars"></a>
</p>

<p align="center">
  <a href="https://olx-india.github.io/AssertX/">Docs</a> ·
  <a href="samples/spring-boot-demo">5-minute demo</a> ·
  <a href="https://olx-india.github.io/AssertX/steps/">Built-in steps</a> ·
  <a href="prompts/">AI prompts</a> ·
  <a href="CONTRIBUTING.md">Contributing</a>
</p>

---

AssertX boots your **Spring Boot or Dropwizard** service, starts the **Redis, MySQL, PostgreSQL, Kafka,
LocalStack, Solr, OpenSearch and HTTP mocks** it depends on, and runs your **Cucumber** scenarios against
it — all from one YAML file. Built at OLX India.

```gherkin
Feature: Greeting API

  Scenario: Greet a known user
    Given I have http://0.0.0.0:9000/ host
    And I have /api/v1/greetings/42 API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "message" is "Hello, Ada!"
```

```yaml
# src/test/resources/api-testing.yml — the whole test environment
mainClass: com.example.greeting.GreetingApplication
application:
  spring:
    profile: integration-test
mocks:
  externalServices:
    userSpecified:
      routes:
        - type: user
          resourcePath: src/test/resources/mocks/external-services/
          folderName: user-service
          endpoints:
            - { methodType: GET, path: /api/v1/users/:userId, method: getUser }
  redis:    { enabled: true }
  postgresql: { enabled: true, dataDir: src/test/resources/postgresql/ }
  kafka:    { enabled: true, topics: [orderCreated] }
  toxiproxy: { enabled: true }   # inject latency / failures into any mock
```

## Why AssertX?

- 🧪 **Readable tests** — Gherkin scenarios that QA, product and developers can all review.
- 🐳 **One YAML, whole environment** — databases, queues, AWS (LocalStack), search engines and HTTP mocks start in Docker before your app boots.
- 🚀 **Boots your service for you** — Spring Boot and Dropwizard, with mock ports injected into your config.
- 💥 **Chaos built in** — Toxiproxy sits in front of every mock so you can test timeouts and failures.
- 📊 **Coverage you can gate on** — JaCoCo integration-test coverage with a minimum threshold.
- 🧼 **SOAP too** — XML downstream mocks for legacy integrations.
- 🤖 **AI-ready** — [prompts](prompts/) that let Claude Code, Cursor or Copilot integrate AssertX and write your tests.
- 🏗️ **CI image included** — [`olxindia/assertx`](https://hub.docker.com/r/olxindia/assertx) runs the suite with Docker-in-Docker.

See [how it compares](https://olx-india.github.io/AssertX/comparison/) with Karate, RestAssured and Testcontainers.

## Quickstart

**Try it (5 minutes):** Java 17+, Maven and Docker running.

```bash
git clone https://github.com/olx-india/AssertX.git
cd AssertX/samples/spring-boot-demo
mvn clean verify
```

Or open the repo in **GitHub Codespaces** — the dev container has Java, Maven and Docker ready.

**Add it to your service:**

```xml
<dependency>
    <groupId>in.olx</groupId>
    <artifactId>assertx</artifactId>
    <version>0.0.5</version> <!-- see the Maven Central badge for the latest -->
    <scope>test</scope>
</dependency>
```

```java
@CucumberOptions(
    features = {"src/test/resources/features"},
    glue = {"com.yourcompany.it.steps"})   // AssertX built-in steps are inherited from ITRunner
public class ITMain extends ITRunner {
}
```

Then add `src/test/resources/api-testing.yml`, write a `.feature` file and run
`./mvnw clean integration-test`. The [integration guide](https://olx-india.github.io/AssertX/getting-started/)
covers failsafe, JaCoCo and CI setup.

## Documentation

| Topic | |
|-------|---|
| Integration guide (Maven, config, execution, reports) | [docs/getting-started.md](docs/getting-started.md) |
| Built-in Cucumber steps and custom steps | [docs/steps.md](docs/steps.md) |
| Mocks: health checks, ports, custom mocks, Redis client | [docs/mocks.md](docs/mocks.md) |
| Gradle (Kotlin DSL) | [docs/gradle.md](docs/gradle.md) |
| Parallel runs, SOAP / XML downstreams | [docs/advanced.md](docs/advanced.md) |
| AI prompts for coding agents | [prompts/](prompts/) |

## Tech stack

| Category   | Technology |
|------------|------------|
| Language   | Java 17 |
| BDD        | Cucumber |
| HTTP       | RestAssured |
| Build      | Maven or Gradle |
| Containers | Docker Compose (Palantir docker-compose-rule) |
| Mocks      | Redis, MySQL, PostgreSQL, Kafka, LocalStack, Solr, OpenSearch, Toxiproxy, Express HTTP mocks |

## Contributing

Issues, ideas and pull requests are welcome — start with [CONTRIBUTING.md](CONTRIBUTING.md) or pick a
[good first issue](https://github.com/olx-india/AssertX/labels/good%20first%20issue).
If AssertX saves you time, a ⭐ helps other teams find it.

## License

[Apache License 2.0](LICENSE)
