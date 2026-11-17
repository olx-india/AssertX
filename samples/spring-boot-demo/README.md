# AssertX Spring Boot demo

A minimal Spring Boot service with an end-to-end AssertX test suite. Use it to see AssertX working in
under five minutes, or as a template for your own service.

```
GET /api/v1/greetings/{userId}  ──►  user-service  GET /api/v1/users/{userId}
                                     (mocked by AssertX in Docker)
```

## Run it

Requirements: Java 17+, Maven, and a running Docker daemon.

```bash
cd samples/spring-boot-demo
mvn clean verify
```

What happens:

1. AssertX reads `src/test/resources/api-testing.yml` and generates a docker-compose file under `it/`.
2. It starts an Express container serving the `user-service` mock (`mocks/external-services/user-service/user.js`).
3. It boots `GreetingApplication` with the `integration-test` profile; `${user-service}` in
   `application-integration-test.yml` is replaced with the mock's port.
4. Cucumber runs `src/test/resources/features/greeting.feature`.
5. An HTML report is written to `target/cucumber-html-reports/`.

## Files to look at

| File | What it shows |
|------|---------------|
| [`ITMain.java`](src/test/java/com/example/greeting/it/ITMain.java) | The one class that wires AssertX in |
| [`greeting.feature`](src/test/resources/features/greeting.feature) | Built-in steps + a Scenario Outline |
| [`ResponseSteps.java`](src/test/java/com/example/greeting/it/steps/ResponseSteps.java) | Writing a custom step |
| [`api-testing.yml`](src/test/resources/api-testing.yml) | Declaring mocks |
| [`user.js`](src/test/resources/mocks/external-services/user-service/user.js) | A downstream HTTP mock |

Next: enable Redis, MySQL, Kafka and more in `api-testing.yml` — see the
[configuration reference](../../docs/getting-started.md#configuration).
