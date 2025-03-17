package com.olx.assertx.configuration;

import com.olx.assertx.application.model.ApplicationFramework;
import com.olx.assertx.mocks.model.MockType;
import com.olx.assertx.service.model.Profile;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class FrameworkConfiguration {
    private Map<Profile, ProfileConfiguration> profileConfig = new HashMap<>();
    private List<ApplicationFramework> availableFrameworks = new ArrayList<>();
    private List<MockType> availableMocks = new ArrayList<>();
    private GlobalMocksConfiguration globalMocks = new GlobalMocksConfiguration();
    private ServiceDefaultsConfiguration serviceDefaults = new ServiceDefaultsConfiguration();
}
