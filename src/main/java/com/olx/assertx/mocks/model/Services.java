package com.olx.assertx.mocks.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "toxiproxy",
        "redis",
        "external-services"
})
public class Services implements Schemaless {

    private Map<String, Object> attributes = new HashMap<>();

    @Override
    public Map<String, Object> getDynamicAttributes() {
        return attributes;
    }

    @Override
    public void setDynamicAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }
}
