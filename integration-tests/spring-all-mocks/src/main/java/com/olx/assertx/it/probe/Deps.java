package com.olx.assertx.it.probe;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Where each dependency lives. In the integration-test profile every port is a placeholder that AssertX
 * fills with a system property named after the mock (e.g. {@code ${redis}}); with Toxiproxy enabled that
 * is the proxy port in front of the container.
 */
@ConfigurationProperties(prefix = "deps")
public record Deps(String host,
                   int redis,
                   int mysql,
                   String mysqlDb,
                   int postgresql,
                   int kafka,
                   int localstack,
                   int solr,
                   int opensearch,
                   int user,
                   int payment,
                   int echo) {

    public String url(int port) {
        return "http://" + host + ":" + port;
    }
}
