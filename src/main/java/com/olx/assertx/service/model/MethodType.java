package com.olx.assertx.service.model;

public enum MethodType {

    GET,
    PUT,
    POST,
    PATCH,
    DELETE;

    public static MethodType fromString(String value) {
        return valueOf(value.toUpperCase());
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
