#!/usr/bin/env bash
# Regression integration test for AssertX itself.
#
# 1. Builds AssertX from the current source and installs it into the local Maven repository.
# 2. Runs the end-to-end suite of every project under samples/ and integration-tests/ (each has its
#    own pom.xml) against that freshly built version, so a change to the library that breaks real
#    usage (mock startup, app boot, built-in steps, reporting) fails here before it is released.
#
# Usage:
#   pipeline-scripts/regression-it.sh                                  # every project
#   pipeline-scripts/regression-it.sh integration-tests/spring-all-mocks # only the named project(s)
#
# Requirements: JDK 17, Maven 3.9+, a running Docker daemon.
# Note: this installs the version from the root pom.xml into ~/.m2, replacing any copy
# downloaded from Maven Central with the same version.
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
MVN="${MVN:-mvn}"
MVN_FLAGS=(-B -ntp)

log() { printf '\n\033[1;34m==> %s\033[0m\n' "$*"; }
fail() { printf '\n\033[1;31mxx %s\033[0m\n' "$*" >&2; exit 1; }

command -v "$MVN" >/dev/null || fail "Maven ('$MVN') not found on PATH"
docker info >/dev/null 2>&1 || fail "Docker daemon is not reachable; start Docker and retry"

java_major="$(java -version 2>&1 | awk -F'"' '/version/ {split($2, v, "."); print (v[1] == "1" ? v[2] : v[1])}')"
if [[ "$java_major" != "17" ]]; then
  echo "warning: building with Java $java_major; AssertX is built and released on Java 17 (Lombok may fail on newer JDKs)" >&2
fi

cd "$ROOT"
VERSION="$("$MVN" "${MVN_FLAGS[@]}" -q help:evaluate -Dexpression=project.version -DforceStdout)"

log "Building and installing AssertX $VERSION from source"
"$MVN" "${MVN_FLAGS[@]}" clean install -Dgpg.skip -Dmaven.javadoc.skip=true -Dmaven.source.skip=true

if [[ $# -gt 0 ]]; then
  projects=("${@%/}")
else
  projects=()
  for pom in "$ROOT"/samples/*/pom.xml "$ROOT"/integration-tests/*/pom.xml; do
    [[ -f "$pom" ]] || continue
    rel="${pom#"$ROOT/"}"
    projects+=("${rel%/pom.xml}")
  done
fi
[[ ${#projects[@]} -gt 0 ]] || fail "no projects with a pom.xml found under samples/ or integration-tests/"

failed=()
for project in "${projects[@]}"; do
  dir="$ROOT/$project"
  [[ -f "$dir/pom.xml" ]] || fail "$project has no pom.xml"
  log "Regression suite: $project (assertx.version=$VERSION)"
  # AssertX resolves api-testing.yml, features and mocks relative to the working directory.
  if (cd "$dir" && "$MVN" "${MVN_FLAGS[@]}" clean verify -Dassertx.version="$VERSION"); then
    echo "PASS $project"
  else
    echo "FAIL $project" >&2
    failed+=("$project")
  fi
done

log "Summary"
for project in "${projects[@]}"; do
  if [[ " ${failed[*]-} " == *" $project "* ]]; then echo "  FAIL  $project"; else echo "  PASS  $project"; fi
done
[[ ${#failed[@]} -eq 0 ]] || fail "${#failed[@]} suite(s) failed: ${failed[*]}"
