docker login ${CI_REGISTRY} -u ${REGISTRY_USER} -p ${REGISTRY_PASSWORD}
./mvnw clean integration-test -DskipUnitTests=true -Dassertx-profile=ci
