# How AssertX compares

AssertX is for teams that want **black-box API tests of a real Java service**, with **its real
dependencies running in Docker**, written in **plain-English Gherkin**.

| | AssertX | Karate | RestAssured + JUnit | Testcontainers |
|---|---|---|---|---|
| Test language | Gherkin (Cucumber) | Karate DSL (Gherkin-like) | Java | Java |
| Boots your Spring Boot / Dropwizard app for you | ✅ | ❌ | ❌ (use `@SpringBootTest`) | ❌ |
| Redis, MySQL, Postgres, Kafka, LocalStack, Solr, OpenSearch from YAML | ✅ | ❌ | ❌ | ✅ (in Java code) |
| Downstream HTTP mocks | ✅ Express JS mocks | ✅ Karate mocks | ❌ (add WireMock) | ❌ (add WireMock module) |
| Fault injection (latency, resets) | ✅ Toxiproxy built in | ❌ | ❌ | ✅ Toxiproxy module |
| Coverage of the service under test | ✅ JaCoCo IT report | ❌ | ✅ | ✅ |
| CI image ready to use | ✅ `olxindia/assertx` | ✅ | — | — |

**Pick AssertX** when you want one YAML file to describe the whole test environment and one Gherkin
file per behaviour, readable by QA and product people.

**Pick something else** if you test APIs you don't own or run (Karate, Postman), or you prefer every
test to be Java code (RestAssured + Testcontainers).
