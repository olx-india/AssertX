package com.olx.assertx.service.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum MethodType {

    GET,
    PUT,
    POST,
    PATCH,
    DELETE,
    HEAD,
    OPTIONS;

    public static MethodType fromString(String value) {
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Unsupported method '" + value + "'. Supported methods: " + supportedMethods(), e);
        }
    }

    private static String supportedMethods() {
        return Arrays.stream(values()).map(MethodType::name).collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
