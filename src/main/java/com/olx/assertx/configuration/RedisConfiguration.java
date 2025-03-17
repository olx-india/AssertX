package com.olx.assertx.configuration;

import lombok.Data;

@Data
public class RedisConfiguration {
    private boolean enabled;
    private int port;
    private String configFilePath;
    private String image;
}