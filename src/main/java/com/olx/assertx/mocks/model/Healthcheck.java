package com.olx.assertx.mocks.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "test",
        "interval",
        "timeout",
        "retries"
})
@Data
@Builder
public class Healthcheck {
    @JsonProperty("test")
    private List<String> test;
    @JsonProperty("interval")
    private String interval;
    @JsonProperty("timeout")
    private String timeout;
    @JsonProperty("retries")
    private Integer retries;
}
