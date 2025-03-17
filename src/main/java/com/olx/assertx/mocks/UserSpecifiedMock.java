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
import java.util.List;

public class UserSpecifiedMock extends ExternalServicesMock implements BaseJSMock {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSpecifiedMock.class);

    @Override
    public MockType getMockType() {
        return MockType.USER_SPECIFIED;
    }

    @Override
    public void load(RouteConfiguration routeConfiguration) throws IOException {
        String destPath = Paths.ES_FOLDER + "/" + getMockType().getServiceName() + "/" + routeConfiguration.getFolderName();
        LOGGER.info("Load route={} for mock={} at path={}", routeConfiguration.getType(), getMockType(), destPath);
        FileUtility.copyDir(routeConfiguration.getResourcePath() + "/" + routeConfiguration.getFolderName(), destPath);
    }

    @Override
    public List<RouteConfiguration> getRouteConfigurations(GlobalMocksConfiguration globalMocksConfiguration,
                                                           UserMocksConfiguration userMocksConfiguration) {
        return userMocksConfiguration.getExternalServices().getUserSpecified().getRoutes();
    }
}
