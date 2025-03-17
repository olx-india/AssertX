package com.olx.assertx.service.impl;

import com.olx.assertx.service.RequestExecutorService;
import com.olx.assertx.context.CucumberTestContext;
import com.olx.assertx.service.executor.RestAssuredRequestExecutor;
import com.olx.assertx.service.model.RequestKey;
import com.olx.assertx.service.model.Request;
import io.restassured.response.Response;

public class RestAssuredExecutorService implements RequestExecutorService {

    private final CucumberTestContext context;
    private final RestAssuredRequestExecutor requestExecutor;

    public RestAssuredExecutorService(final CucumberTestContext context) {
        this.context = context;
        this.requestExecutor = new RestAssuredRequestExecutor();
    }

    @Override
    public void execute(Request request) {
        Response response = this.requestExecutor.executeRequest(request);
        this.context.set(RequestKey.RESPONSE.toName(), response);
    }

}
