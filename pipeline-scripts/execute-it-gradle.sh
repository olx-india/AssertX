docker login ${CI_REGISTRY} -u ${CI_REGISTRY_USER} -p ${CI_REGISTRY_PASSWORD}
./gradlew integrationTest jacocoIntegrationTestReport jacocoTestCoverageVerification -Passertx-profile=ci