package com.olx.assertx.service.model;

import lombok.Data;

import java.util.Map;


@Data
public class Request {
    private String baseURL;
    private String apiPath;
    private String requestType;
    private Map<String, String> headers;
    private Map<String, String> queryParameters;
    private Map<String, String> multipartFileSpecifications;
    private String requestBodyJson;
}
