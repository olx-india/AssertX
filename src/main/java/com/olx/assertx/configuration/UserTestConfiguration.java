package com.olx.assertx.configuration;

import lombok.Data;

@Data
public class UserTestConfiguration {
    private String mainClass;
    private UserApplicationConfiguration application = new UserApplicationConfiguration();
    private UserMocksConfiguration mocks = new UserMocksConfiguration();
}
