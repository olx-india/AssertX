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
    "volumes",
    "networks"
})
@Getter
@Setter
public class OpenSearch extends BaseService {

  private String image;
  private List<String> volumes;

  @Builder
  public OpenSearch(String containerName,
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
