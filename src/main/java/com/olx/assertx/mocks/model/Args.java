package com.olx.assertx.mocks.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "TP_DYNAMIC_PORT",
        "ES_DYNAMIC_PORT"
})
@Data
@Builder
public class Args {
    @JsonProperty("TP_DYNAMIC_PORT")
    private String tpDynamicPort;
    @JsonProperty("ES_DYNAMIC_PORT")
    private String esDynamicPort;
}