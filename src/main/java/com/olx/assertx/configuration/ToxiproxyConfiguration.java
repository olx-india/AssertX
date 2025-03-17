package com.olx.assertx.configuration;

import lombok.Data;

@Data
public class ToxiproxyConfiguration {
    private boolean enabled;
    private int port;
}