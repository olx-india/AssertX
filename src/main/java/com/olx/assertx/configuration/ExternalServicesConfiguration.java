package com.olx.assertx.configuration;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ExternalServicesConfiguration {
    private int port;
    private List<String> global = new ArrayList<>();
    private UserSpecifiedConfiguration userSpecified = new UserSpecifiedConfiguration();
    private List<DependencyConfiguration> dependencies = new ArrayList<>();
}
