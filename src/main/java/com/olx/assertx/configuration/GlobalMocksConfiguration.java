package com.olx.assertx.configuration;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GlobalMocksConfiguration {
    private List<RouteConfiguration> routes = new ArrayList<>();
}
