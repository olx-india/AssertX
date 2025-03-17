package com.olx.assertx.mocks;

import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.RouteConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.MockType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public interface BaseJSMock {
    Logger LOGGER = LoggerFactory.getLogger(BaseJSMock.class);
    String TEMPLATE_SERVICE_DEFINITION = "var %s = require('%s');";
    String TEMPLATE_PATH_DEFINITION = "app.%s('%s', %s.%s);";

    default void configure(RouteConfiguration routeConfiguration, StringBuilder serviceDefinitions,
                           StringBuilder pathDefinitions, String serviceType) {
        LOGGER.info("Configure route of type={} and service={}", routeConfiguration.getType(), serviceType);

        String serviceId = routeConfiguration.getType() + StringUtils.capitalize(serviceType.split("-")[0]);

        serviceDefinitions.append(String.format(
                TEMPLATE_SERVICE_DEFINITION,
                serviceId,
                "./" + serviceType + "/" + routeConfiguration.getFolderName() + "/" + routeConfiguration.getType())
        ).append("\n");

        routeConfiguration.getEndpoints()
                .forEach(endpointConfiguration ->
                        pathDefinitions.append(String.format(
                                TEMPLATE_PATH_DEFINITION,
                                endpointConfiguration.getMethodType().toString(), endpointConfiguration.getPath(),
                                serviceId, endpointConfiguration.getMethod())
                        ).append("\n"));
    }

    default void build(GlobalMocksConfiguration globalMocksConfiguration,
                       UserMocksConfiguration userMocksConfiguration,
                       StringBuilder serviceDefinitions, StringBuilder pathDefinitions) throws IOException {
        LOGGER.info("Build all JS mock routes");
        for (RouteConfiguration routeConfiguration :
                getRouteConfigurations(globalMocksConfiguration, userMocksConfiguration)) {
            configure(routeConfiguration, serviceDefinitions, pathDefinitions, getMockType().getServiceName());
            load(routeConfiguration);
        }
    }

    void load(RouteConfiguration routeConfiguration) throws IOException;

    List<RouteConfiguration> getRouteConfigurations(GlobalMocksConfiguration globalMocksConfiguration,
                                                    UserMocksConfiguration userMocksConfiguration);

    MockType getMockType();

}
