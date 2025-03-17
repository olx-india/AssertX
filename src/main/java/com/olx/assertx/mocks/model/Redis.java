package com.olx.assertx.mocks.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "image",
        "container_name",
        "user",
        "ports",
        "environment",
        "healthcheck",
        "networks"
})
@Getter
@Setter
public class Redis extends BaseService {
    private String image;
    private String user;
    private List<String> volumes;
    private List<String> command;

    @Builder
    public Redis(String containerName,
                 List<String> ports,
                 List<String> environment,
                 Networks networks,
                 Healthcheck healthcheck,
                 String image,
                 String user,
                 List<String> volumes,
                 List<String> command) {
        super(containerName, ports, environment, networks, healthcheck);
        this.image = image;
        this.user = user;
        this.volumes = volumes;
        this.command = command;
    }
}
