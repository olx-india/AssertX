package com.olx.assertx.mocks.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "image",
    "container_name",
    "ports",
    "environment",
    "healthcheck",
    "volumes",
    "networks"
})
public class Mysql extends BaseService {

  private String image;
  private List<String> volumes;

  @Builder
  public Mysql(String containerName,
               List<String> ports,
               List<String> environment,
               Networks networks,
               Healthcheck healthcheck,
               String image,
               List<String> volumes) {
    super(containerName, ports, environment, networks, healthcheck);
    this.image = image;
    this.volumes = volumes;
  }

}
