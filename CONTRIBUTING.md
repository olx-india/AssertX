# Contributing to AssertX

Thanks for helping! Bug reports, docs fixes, new mocks and new steps are all welcome.

## Ways to help

- **Report a bug or ask for a feature** — [open an issue](https://github.com/olx-india/AssertX/issues/new/choose).
- **Ask a question or share how you use AssertX** — [GitHub Discussions](https://github.com/olx-india/AssertX/discussions).
- **Pick up work** — issues labelled [`good first issue`](https://github.com/olx-india/AssertX/labels/good%20first%20issue) or [`help wanted`](https://github.com/olx-india/AssertX/labels/help%20wanted).
- **Improve the docs** — every page under [`docs/`](docs/) has an edit link on the docs site.

## Development setup

Requirements: JDK 17 to build the library (newer JDKs need extra Lombok setup), Maven 3.9+, Docker.

```bash
git clone https://github.com/olx-india/AssertX.git
cd AssertX
mvn -B clean package -Dgpg.skip      # build the library
mvn -B install -Dgpg.skip            # install a local snapshot for the samples
cd samples/spring-boot-demo
mvn clean verify -Dassertx.version=<version from the root pom.xml>
```

### Regression integration tests

`pipeline-scripts/regression-it.sh` builds AssertX from your working tree, installs it into `~/.m2`, then
runs the end-to-end suite of every project under `samples/` and `integration-tests/` against that build.
`integration-tests/spring-all-mocks` enables every built-in mock at once behind Toxiproxy and exercises
every built-in step; `integration-tests/dropwizard-app` covers Dropwizard, Toxiproxy off and port/image
overrides. CI runs the same script on every pull request
([`regression-it.yml`](.github/workflows/regression-it.yml)).

```bash
pipeline-scripts/regression-it.sh                                    # everything
pipeline-scripts/regression-it.sh integration-tests/spring-all-mocks # one project
```

If you change a mock, a step or the runner, add or extend a scenario there — see
[`integration-tests/README.md`](integration-tests/README.md). The script installs the root pom's version
locally, replacing any Maven Central copy of it.

Or open the repository in GitHub Codespaces / VS Code Dev Containers; `.devcontainer/` has everything.

Docs preview:

```bash
pip install mkdocs-material
mkdocs serve
```

## Project layout

| Path | What lives there |
|------|------------------|
| `src/main/java/com/olx/assertx/ITRunner.java` | JUnit/Cucumber runner that starts mocks and the app |
| `src/main/java/com/olx/assertx/mocks/` | One class per mock (Redis, Kafka, …) and the docker-compose model |
| `src/main/java/com/olx/assertx/configuration/` | `api-testing.yml` schema |
| `src/main/java/com/olx/assertx/stepdefinitions/` | Built-in Cucumber steps |
| `src/main/resources/config.yml` | Default images and ports |
| `samples/` | Runnable examples |
| `docs/` | Documentation site (MkDocs) |

## Adding a new mock

1. Add a configuration class in `configuration/` and a field in `UserMocksConfiguration`.
2. Add a docker-compose service model in `mocks/model/` and a mock class implementing `BaseMock` in `mocks/`.
3. Register it in `MockType`, the mock factory, and `availableMocks` / `serviceDefaults` in `config.yml`.
4. Document it in `docs/getting-started.md` (the `api-testing.yml` example) and `docs/mocks.md`.

## Pull requests

- Keep each PR to one change; describe the motivation and how you tested it.
- Run `mvn -B clean package -Dgpg.skip` before pushing.
- Update the docs when behaviour or configuration changes.
- By contributing you agree your work is licensed under the [Apache License 2.0](LICENSE) and you follow our [Code of Conduct](CODE_OF_CONDUCT.md).
