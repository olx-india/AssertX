# AssertX regression integration tests

These projects test **AssertX itself**: each one is a real service that depends on AssertX, and its
Cucumber suite only passes if AssertX starts every container correctly, injects the right ports, boots
the app and runs the built-in steps. They run against the AssertX version built from this repository.

```bash
pipeline-scripts/regression-it.sh                                   # every project here and in samples/
pipeline-scripts/regression-it.sh integration-tests/spring-all-mocks # one project
```

Requirements: JDK 17, Maven 3.9+, Docker. CI runs the same script in
[`regression-it.yml`](../.github/workflows/regression-it.yml), one job per project.

## What is covered

| Project | Covers |
|---------|--------|
| [`spring-all-mocks`](spring-all-mocks) | Spring Boot app with **every built-in mock enabled at once, behind Toxiproxy**: Redis (`configFilePath`), MySQL (`db`, `dataDir`, custom `port`), PostgreSQL (`dataDir`, custom `port`), Kafka + ZooKeeper (`topics`), LocalStack S3 (`dataDir` init script), Solr (`collectionsDir`, `collections`), OpenSearch, JS HTTP mocks (several routes and endpoints, npm `dependencies`), a SOAP/XML mock, and a custom mock (`mocks.custom`). Every built-in step (all HTTP methods, query params, headers, body from file and from context, multipart, 301 redirect, status and header checks), `getJedisClient()`, and Toxiproxy latency / outage via `getToxiProxyClient()`. |
| [`dropwizard-app`](dropwizard-app) | Dropwizard boot (`application.dropwizard.configPath`), **Toxiproxy disabled** (direct container ports), custom `externalServices.port`, custom Redis `port` and `image`. |
| [`../samples/spring-boot-demo`](../samples/spring-boot-demo) | The quick-start demo, kept green so the README stays true. |

The probe app in `spring-all-mocks` has one endpoint per dependency that does a real round trip
(write then read), so a mock that starts but is unreachable, mis-ported, unseeded or on the wrong network
fails a scenario. Each feature first waits on `GET /ready/<dependency>`.

## Adding coverage

- New behaviour of an existing mock: add a scenario to the matching feature in
  `spring-all-mocks/src/test/resources/features/` and, if needed, an endpoint to the probe app.
- A configuration that cannot coexist with the others (e.g. Toxiproxy off, another framework): add a new
  folder here with its own `pom.xml` reading `${assertx.version}` — the script and CI pick it up.

## Running locally

AssertX publishes fixed host ports (Redis 6379, Kafka 9093, Solr 8983, OpenSearch 9200, LocalStack 4566,
…). Stop anything else listening on them first. `spring-all-mocks` uses 33306 for MySQL and 35432 for
PostgreSQL so a local database does not get in the way. Containers are named `assertx-<mock>`.
The first run pulls about 4 GB of images.
