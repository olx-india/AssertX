package com.olx.assertx.it.dropwizard;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;

public class GreetingConfiguration extends Configuration {

    @JsonProperty
    private String userServiceUrl;

    @JsonProperty
    private String redisHost;

    @JsonProperty
    private int redisPort;

    public String getUserServiceUrl() {
        return userServiceUrl;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }
}
