package com.olx.assertx.mocks.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "image",
        "container_name",
        "ports",
        "environment",
        "entrypoint",
        "volumes",
        "healthcheck",
        "networks"
})
@Getter
@Setter
public class Kafka extends BaseService {
    String image;
    List<String> entrypoint;
    List<String> volumes;

    @Builder
    public Kafka(String containerName,
                 List<String> ports,
                 List<String> environment,
                 Networks networks,
                 Healthcheck healthcheck,
                 String image,
                 List<String> entrypoint,
                 List<String> volumes) {
        super(containerName, ports, environment, networks, healthcheck);
        this.image = image;
        this.entrypoint = entrypoint;
        this.volumes = volumes;
    }
}
