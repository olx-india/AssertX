package com.olx.assertx.it.probe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Probe service for AssertX regression tests. Every endpoint performs a real round trip against one
 * dependency that AssertX starts in Docker, so a broken mock (wrong port, image, volume, network,
 * proxy wiring) surfaces as a failing scenario.
 */
@SpringBootApplication
@EnableConfigurationProperties(Deps.class)
public class ProbeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProbeApplication.class, args);
    }
}
