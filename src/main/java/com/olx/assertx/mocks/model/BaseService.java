package com.olx.assertx.mocks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BaseService {
    @JsonProperty("container_name")
    private String containerName;
    private List<String> ports;
    private List<String> environment;
    private Networks networks;
    private Healthcheck healthcheck;
}
