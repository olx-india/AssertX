package com.olx.assertx.service.model;

public enum PropertyKey {
    PROFILE("assertx-profile"),
    DOCKER_HOST("docker-host");

    private final String value;

    PropertyKey(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
