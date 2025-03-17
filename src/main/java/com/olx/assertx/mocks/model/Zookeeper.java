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
        "healthcheck",
        "networks"
})
@Getter
@Setter
public class Zookeeper extends BaseService {
    String image;

    @Builder
    public Zookeeper(String containerName,
                     List<String> ports,
                     List<String> environment,
                     Networks networks,
                     Healthcheck healthcheck,
                     String image) {
        super(containerName, ports, environment, networks, healthcheck);
        this.image = image;
    }
}
