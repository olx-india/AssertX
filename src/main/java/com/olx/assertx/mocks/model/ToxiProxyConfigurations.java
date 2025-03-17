package com.olx.assertx.mocks.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "listen",
        "upstream"
})
@Data
@Builder
public class ToxiProxyConfigurations {
    @JsonProperty("name")
    private String name;
    @JsonProperty("listen")
    private String listen;
    @JsonProperty("upstream")
    private String upstream;
}
