package com.olx.assertx.configuration;

import lombok.Data;

import java.util.List;

@Data
public class KafkaConfiguration {
    private boolean enabled;
    private int port;
    private String image;
    private List<String> topics;
}
