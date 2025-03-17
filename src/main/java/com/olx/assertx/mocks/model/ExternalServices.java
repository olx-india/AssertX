package com.olx.assertx.mocks.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "build",
        "container_name",
        "ports",
        "networks",
        "healthcheck",
        "environment"
})
@Getter
@Setter
public class ExternalServices extends BaseService {
    private String build;

    @Builder
    public ExternalServices(String containerName,
                            List<String> ports,
                            List<String> environment,
                            Networks networks,
                            Healthcheck healthcheck,
                            String build) {
        super(containerName, ports, environment, networks, healthcheck);
        this.build = build;
    }
}
