package com.olx.assertx.service.mapper;

import com.olx.assertx.context.CucumberTestContext;
import com.olx.assertx.service.model.Request;

public interface RequestMapper {

    Request map(CucumberTestContext context);

}
