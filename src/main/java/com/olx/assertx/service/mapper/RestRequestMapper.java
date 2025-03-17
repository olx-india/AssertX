package com.olx.assertx.service.mapper;

import com.olx.assertx.context.CucumberTestContext;
import com.olx.assertx.service.model.Request;
import com.olx.assertx.service.model.RequestKey;

import java.util.Map;

public class RestRequestMapper implements RequestMapper {

    public Request map(CucumberTestContext context) {
        Request request = new Request();
        if (context.contains(RequestKey.HOST.toName())) {
            request.setBaseURL(context.get(RequestKey.HOST.toName(), String.class));
        }

        if (context.contains(RequestKey.API_PATH.toName())) {
            request.setApiPath(context.get(RequestKey.API_PATH.toName(), String.class));
        }

        if (context.contains(RequestKey.TYPE.toName())) {
            request.setRequestType(context.get(RequestKey.TYPE.toName(), String.class));
        }

        if (context.contains(RequestKey.QUERY_PARAMS.toName())) {
            request.setQueryParameters((Map<String, String>) context.get(RequestKey.QUERY_PARAMS.toName()));
        }

        if (context.contains(RequestKey.HEADERS.toName())) {
            request.setHeaders((Map<String, String>) context.get(RequestKey.HEADERS.toName()));
        }

        if (context.contains(RequestKey.PAYLOAD.toName())) {
            request.setRequestBodyJson(context.get(RequestKey.PAYLOAD.toName(), String.class));
        }

        if(context.contains(RequestKey.MULTIPART_SPECS.toName())){
            request.setMultipartFileSpecifications((Map<String, String>) context.get(RequestKey.MULTIPART_SPECS.toName()));
        }

        return request;
    }

}
