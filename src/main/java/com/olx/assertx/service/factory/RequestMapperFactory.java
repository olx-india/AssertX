package com.olx.assertx.service.factory;

import com.olx.assertx.service.mapper.RequestMapper;
import com.olx.assertx.service.mapper.RestRequestMapper;
import com.olx.assertx.service.model.RequestType;

public class RequestMapperFactory {

    public static RequestMapper getRequestMapper(RequestType type) {
        RequestMapper requestMapper = null;
        if (type.equals(RequestType.REST)) {
            requestMapper = new RestRequestMapper();
        }
        return requestMapper;
    }

}
