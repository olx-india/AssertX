package com.olx.assertx.mocks.model;

public enum MockType {
    EXTERNAL_SERVICES("external-services"),
    GLOBAL("global"),
    USER_SPECIFIED("user-specified"),
    REDIS("redis"),
    TOXIPROXY("toxiproxy"),
    MYSQL("mysql"),
    LOCALSTACK("localstack"),
    SOLR("solr"),
    ZOOKEEPER("zookeeper"),
    KAFKA("kafka"),
    POSTGRESQL("postgresql"),
    OPENSEARCH("opensearch");

    private String serviceName;

    MockType(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return this.serviceName;
    }

}
