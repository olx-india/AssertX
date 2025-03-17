package com.olx.assertx.configuration;

import lombok.Data;

@Data
public class UserApplicationConfiguration {
    private SpringApplicationConfiguration spring;
    private DropwizardApplicationConfiguration dropwizard;
}
