package com.olx.assertx.mocks;

import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.BaseService;
import com.olx.assertx.mocks.model.Services;
import com.olx.assertx.utils.PortManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public interface BaseCustomMock {
    Logger LOGGER = LoggerFactory.getLogger(BaseCustomMock.class);

    default void build(UserMocksConfiguration userMocksConfiguration,
                       Services services)
            throws IOException {
        if (isEnabled()) {
            LOGGER.info("Building {} custom mock", getServiceName());
            configure(userMocksConfiguration);
            services.setDynamicAttribute(getServiceName(), map(PortManager.getInstance().getServicePort(getServiceName())));
        }
    }

    default void configure(UserMocksConfiguration userMocksConfiguration) {
        LOGGER.info("Configure proxy ports for mock={}", getServiceName());
        PortManager.getInstance().addServicePort(getServiceName(), getServicePort());
        if (userMocksConfiguration.getToxiproxy().isEnabled()) {
            PortManager.getInstance().addProxyPort(getServiceName());
        }
    }

    boolean isEnabled();

    BaseService map(int port);

    String getServiceName();

    int getServicePort();

}
