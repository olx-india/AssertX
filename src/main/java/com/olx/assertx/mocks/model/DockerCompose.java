package com.olx.assertx.mocks.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DockerCompose {
    private Services services;
    private Networks networks;
}
