package com.olx.assertx.service.model;

public enum Profile {
    LOCAL,
    CI;

    public static Profile fromString(String value) {
        return valueOf(value.toUpperCase());
    }
}
