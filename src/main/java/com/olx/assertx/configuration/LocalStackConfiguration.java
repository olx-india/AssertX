package com.olx.assertx.configuration;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LocalStackConfiguration {
    private boolean enabled;
    private List<String> services = new ArrayList<>();
    private String region;
    private String image;
    private int port;
    private String dataDir;
}
