package com.olx.assertx.configuration;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RouteConfiguration {
    private String type;
    private String resourcePath;
    private String folderName;
    private List<String> fileNames = new ArrayList<>();
    private List<EndpointConfiguration> endpoints = new ArrayList<>();
}