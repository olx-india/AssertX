package com.olx.assertx.mocks;

import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.BaseService;
import com.olx.assertx.mocks.model.MockType;
import com.olx.assertx.mocks.model.Services;
import com.olx.assertx.utils.FileUtility;
import com.olx.assertx.utils.Paths;
import com.olx.assertx.utils.PortManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;

public interface BaseMock {
    Logger LOGGER = LoggerFactory.getLogger(BaseMock.class);
    String PORT_MAPPING_FORMAT = "%s:%s";
    String ENV_FORMAT = "%s=%s";
    String DOCKER_FILENAME = "Dockerfile";
    String DOCKERFILE_TEMPLATE_PATH_FORMAT = Paths.TEMPLATE_PATH_FORMAT + "/" + DOCKER_FILENAME;
    String TARGET_DOCKERFILE_PATH_FORMAT = Paths.MOCKS_FOLDER + "/%s";
    String DOCKERFILE_PORT_PLACEHOLDER = "${DYNAMIC_PORT}";

    default void configureAndLoadDockerFile() throws IOException {
        int port = PortManager.getInstance().getServicePort(getMockType().getServiceName());
        LOGGER.info("Configure and load docker file for mock={} on port={}", getMockType(), port);
        FileUtility.generateFileFromTemplate(
                String.format(DOCKERFILE_TEMPLATE_PATH_FORMAT, getMockType().getServiceName()),
                String.format(TARGET_DOCKERFILE_PATH_FORMAT, getMockType().getServiceName()),
                DOCKER_FILENAME,
                Collections.singletonMap(DOCKERFILE_PORT_PLACEHOLDER, String.valueOf(port))
        );
    }

    default void build(FrameworkConfiguration frameworkConfiguration,
                       UserMocksConfiguration userMocksConfiguration,
                       Services services)
            throws IOException {
        if (isEnabled(userMocksConfiguration)) {
            LOGGER.info("Building {} mock", getMockType());
            configure(frameworkConfiguration, userMocksConfiguration);

            copyStartupFiles(frameworkConfiguration.getGlobalMocks(), userMocksConfiguration);

            services.setDynamicAttribute(getMockType().getServiceName(),
                    map(frameworkConfiguration, userMocksConfiguration, getMockType().getServiceName()));
        }
    }

    default void configure(FrameworkConfiguration frameworkConfiguration,
                           UserMocksConfiguration userMocksConfiguration) {
        LOGGER.info("Configure proxy ports for mock={}", getMockType());
        PortManager.getInstance().addServicePort(getMockType().getServiceName(), getServicePort(frameworkConfiguration, userMocksConfiguration));
        addCustomServicePorts(frameworkConfiguration, userMocksConfiguration);
        if (userMocksConfiguration.getToxiproxy().isEnabled()
                && getMockType() != MockType.TOXIPROXY) {
            PortManager.getInstance().addProxyPort(getMockType().getServiceName());
            addCustomProxyPorts(userMocksConfiguration);
        }
    }

    default void addCustomServicePorts(FrameworkConfiguration frameworkConfiguration,
                                       UserMocksConfiguration userMocksConfiguration) {}

    default void addCustomProxyPorts(UserMocksConfiguration userMocksConfiguration) {}

    boolean isEnabled(UserMocksConfiguration userMocksConfiguration);

    BaseService map(FrameworkConfiguration frameworkConfiguration, UserMocksConfiguration userMocksConfiguration,
                    String serviceName);

    MockType getMockType();

    String getDockerImage(FrameworkConfiguration frameworkConfiguration,
                          UserMocksConfiguration userMocksConfiguration);

    int getServicePort(FrameworkConfiguration frameworkConfiguration, UserMocksConfiguration userMocksConfiguration);

    void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                          UserMocksConfiguration userMocksConfiguration) throws IOException;

}
