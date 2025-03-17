package com.olx.assertx.mocks.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public interface Schemaless {

    @JsonAnyGetter
    Map<String, Object> getDynamicAttributes();

    void setDynamicAttributes(Map<String, Object> attributes);

    default void addDynamicAttributes(Map<String, Object> attributes) {
        getDynamicAttributes().putAll(attributes);
    }

    default Object getDynamicAttribute(String name) {
        return getDynamicAttributes().get(name);
    }

    @JsonAnySetter
    default void setDynamicAttribute(String name, Object value) {
        getDynamicAttributes().put(name, value);
    }

}
