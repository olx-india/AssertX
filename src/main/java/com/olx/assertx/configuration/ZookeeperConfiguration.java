package com.olx.assertx.configuration;

import lombok.Data;

@Data
public class ZookeeperConfiguration {
    private boolean enabled;
    private int port;
    private String image;
}
