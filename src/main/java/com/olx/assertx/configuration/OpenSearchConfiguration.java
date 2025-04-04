package com.olx.assertx.configuration;

import lombok.Data;

import java.util.List;

@Data
public class OpenSearchConfiguration {
    private boolean enabled;
    private int port;
    private String image;
    private List<Integer> extraPorts;
    private List<VolumeConfiguration> volumes;
}
