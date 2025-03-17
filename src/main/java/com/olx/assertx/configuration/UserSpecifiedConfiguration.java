package com.olx.assertx.configuration;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserSpecifiedConfiguration {
    private List<RouteConfiguration> routes = new ArrayList<>();
}
