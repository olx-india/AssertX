package com.olx.assertx.configuration;

import com.olx.assertx.service.model.MethodType;
import lombok.Data;

@Data
public class EndpointConfiguration {
    private MethodType methodType;
    private String path;
    private String method;
}
