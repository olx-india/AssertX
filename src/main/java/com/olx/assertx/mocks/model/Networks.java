package com.olx.assertx.mocks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

@JsonPropertyOrder({
        "service1_net"
})
@Data
@Builder
public class Networks {
    @JsonProperty("service1_net")
    private Service1Net service1NetName;
}
