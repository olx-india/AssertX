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
        "healthcheck",
        "networks",
        "environment",
        "restart"
})
@Getter
@Setter
public class ToxiProxy extends BaseService {
    private String build;
    private String restart;

    @Builder
    public ToxiProxy(String containerName,
                     List<String> ports,
                     List<String> environment,
                     Networks networks,
                     Healthcheck healthcheck,
                     String build,
                     String restart) {
        super(containerName, ports, environment, networks, healthcheck);
        this.build = build;
        this.restart = restart;
    }
}
