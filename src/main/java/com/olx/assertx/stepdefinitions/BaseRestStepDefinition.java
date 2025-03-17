package com.olx.assertx.stepdefinitions;

import com.olx.assertx.service.RequestExecutorService;
import com.olx.assertx.service.factory.RequestExecutorServiceFactory;
import com.olx.assertx.service.factory.RequestMapperFactory;
import com.olx.assertx.service.mapper.RequestMapper;
import com.olx.assertx.service.model.Request;
import com.olx.assertx.service.model.RequestType;

public class BaseRestStepDefinition extends BaseStepDefinition {

    private final RequestExecutorService requestExecutorService;
    private final RequestMapper requestMapper;

    public BaseRestStepDefinition() {
        super();
        this.requestExecutorService = RequestExecutorServiceFactory.getRequestExecutorService(RequestType.REST, testContext());
        this.requestMapper = RequestMapperFactory.getRequestMapper(RequestType.REST);
    }

    public RequestMapper requestMapper() {
        return requestMapper;
    }

    public void executeRequest(Request request) {
        this.requestExecutorService.execute(request);
    }

}
