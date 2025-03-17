package com.olx.assertx.service.factory;

import com.olx.assertx.context.CucumberTestContext;
import com.olx.assertx.service.RequestExecutorService;
import com.olx.assertx.service.impl.RestAssuredExecutorService;
import com.olx.assertx.service.model.RequestType;

public class RequestExecutorServiceFactory {

    public static RequestExecutorService getRequestExecutorService(RequestType type, CucumberTestContext context) {
        RequestExecutorService requestExecutorService = null;
        if (type.equals(RequestType.REST)) {
            requestExecutorService = new RestAssuredExecutorService(context);
        }
        return requestExecutorService;
    }

}
