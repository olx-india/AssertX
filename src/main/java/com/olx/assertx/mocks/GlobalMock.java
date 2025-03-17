package com.olx.assertx.mocks;

import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.RouteConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.MockType;
import com.olx.assertx.utils.FileUtility;
import com.olx.assertx.utils.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalMock extends ExternalServicesMock implements BaseJSMock {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalMock.class);

    @Override
    public MockType getMockType() {
        return MockType.GLOBAL;
    }

    @Override
    public void load(RouteConfiguration routeConfiguration) throws IOException {
        String destPath = Paths.ES_FOLDER + "/" + getMockType().getServiceName() + "/" + routeConfiguration.getFolderName();
        LOGGER.info("Load route={} for mock={} at path={}", routeConfiguration.getType(), getMockType(), destPath);
        FileUtility.copyJarDir(
                routeConfiguration.getResourcePath() + "/" + routeConfiguration.getFolderName(),
                destPath, routeConfiguration.getFileNames()
        );
    }

    @Override
    public List<RouteConfiguration> getRouteConfigurations(GlobalMocksConfiguration globalMocksConfiguration,
                                                           UserMocksConfiguration userMocksConfiguration) {
        LOGGER.info("Get route configurations for mock={}", getMockType());
        Map<String, RouteConfiguration> globalServiceRoutes = loadRoutes(globalMocksConfiguration.getRoutes());
        List<RouteConfiguration> routeConfigurations = new ArrayList<>();
        for (String route : userMocksConfiguration.getExternalServices().getGlobal()) {
            if (globalServiceRoutes.containsKey(route)) {
                routeConfigurations.add(globalServiceRoutes.get(route));
            }
        }
        return routeConfigurations;
    }

    private Map<String, RouteConfiguration> loadRoutes(List<RouteConfiguration> routeConfigurationList) {
        Map<String, RouteConfiguration> routesMap = new HashMap<>();
        for (RouteConfiguration route : routeConfigurationList) {
            routesMap.put(route.getType(), route);
        }
        return routesMap;
    }
}
