package com.olx.assertx.configuration;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OpenSearchConfiguration {
    private boolean enabled;
    private int port;
    private String image;
    private List<Integer> extraPorts = new ArrayList<>();
    private List<VolumeConfiguration> volumes = new ArrayList<>();
}
