package com.olx.assertx.mocks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.olx.assertx.configuration.DependencyConfiguration;
import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.factory.MockFactory;
import com.olx.assertx.mocks.model.*;
import com.olx.assertx.utils.FileUtility;
import com.olx.assertx.utils.Paths;
import com.olx.assertx.utils.PortManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ExternalServicesMock implements BaseMock {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalServicesMock.class);
    private static final String PACKAGE_JSON_FILE = "/package.json";
    private static final String PACKAGE_JSON_FILE_TEMPLATE = "/package.json.template";
    private static final String APP_JS_FILE = "/app.js";
    private static final String GITIGNORE_FILE = "/.gitignore";
    private static final String SERVICE_DEFINITIONS_PLACEHOLDER = "${SERVICE_DEFINITIONS}";
    private static final String PATH_DEFINITIONS_PLACEHOLDER = "${PATH_DEFINITIONS}";
    private static final String SERVICE_PORT_PLACEHOLDER = "${SERVICE_PORT}";
    private static final String DEPENDENCY_DEFINITIONS_PLACEHOLDER = "${DEPENDENCY_DEFINITIONS}";
    private static final String TEMPLATE_DEPENDENCY_DEFINITION = "\"%s\": \"%s\"";

    private static final List<MockType> availableJSMocks = ImmutableList.of(
            MockType.GLOBAL,
            MockType.USER_SPECIFIED
    );

    @Override
    public void addCustomServicePorts(FrameworkConfiguration frameworkConfiguration,
                                      UserMocksConfiguration userMocksConfiguration) {
        LOGGER.info("Add custom service ports for mock={}", getMockType());
        int servicePort = getServicePort(frameworkConfiguration, userMocksConfiguration);
        userMocksConfiguration.getExternalServices().getUserSpecified().getRoutes()
                .forEach(route -> PortManager.getInstance().addServicePort(route.getType(), servicePort));
        userMocksConfiguration.getExternalServices().getGlobal()
                .forEach(serviceName -> PortManager.getInstance().addServicePort(serviceName, servicePort));
    }

    @Override
    public void addCustomProxyPorts(UserMocksConfiguration userMocksConfiguration) {
        LOGGER.info("Add custom proxy ports for mock={}", getMockType());
        userMocksConfiguration.getExternalServices().getUserSpecified().getRoutes()
                .forEach(route -> PortManager.getInstance().addProxyPort(route.getType()));
        userMocksConfiguration.getExternalServices().getGlobal()
                .forEach(serviceName -> PortManager.getInstance().addProxyPort(serviceName));
    }

    @Override
    public boolean isEnabled(UserMocksConfiguration userMocksConfiguration) {
        return !userMocksConfiguration.getExternalServices().getGlobal().isEmpty()
                || !userMocksConfiguration.getExternalServices().getUserSpecified().getRoutes().isEmpty();
    }

    @Override
    public ExternalServices map(FrameworkConfiguration frameworkConfiguration,
                                UserMocksConfiguration userMocksConfiguration,
                                String serviceName) {
        int port = PortManager.getInstance().getServicePort(serviceName);

        LOGGER.info("Map {} mock on port={}", getMockType(), port);

        return ExternalServices.builder()
                .build(serviceName)
                .containerName(serviceName)
                .ports(Collections.singletonList(String.format(PORT_MAPPING_FORMAT, port, port)))
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder().build())
                        .build())
                .healthcheck(Healthcheck.builder()
                        .test(List.of("CMD", "curl", "-sf", "localhost:" + port + "/healthcheck"))
                        .interval("2s")
                        .retries(3)
                        .timeout("5s")
                        .build())
                .build();
    }

    @Override
    public MockType getMockType() {
        return MockType.EXTERNAL_SERVICES;
    }

    @Override
    public String getDockerImage(FrameworkConfiguration frameworkConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) {
        return null;
    }

    @Override
    public int getServicePort(FrameworkConfiguration frameworkConfiguration,
                              UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getExternalServices().getPort() > 0) {
            return userMocksConfiguration.getExternalServices().getPort();
        }
        return frameworkConfiguration.getServiceDefaults().getExternalServices().getPort();
    }

    @Override
    public void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) throws IOException {
        LOGGER.info("Copy startup files for mock={}", getMockType());

        configureAndLoadDockerFile();

        String dependencies = getDependencies(userMocksConfiguration);

        StringBuilder serviceDefinitions = new StringBuilder();
        StringBuilder pathDefinitions = new StringBuilder();
        for (MockType mockType : availableJSMocks) {
            BaseJSMock mock = MockFactory.getJSMock(mockType);
            mock.build(globalMocksConfiguration, userMocksConfiguration, serviceDefinitions, pathDefinitions);
        }

        // Load app.js, gitignore and package.json.template
        FileUtility.generateFileFromTemplate(
                Paths.TEMPLATE_ES_PATH + APP_JS_FILE,
                Paths.ES_FOLDER,
                APP_JS_FILE,
                ImmutableMap.of(
                        SERVICE_DEFINITIONS_PLACEHOLDER, serviceDefinitions.toString(),
                        PATH_DEFINITIONS_PLACEHOLDER, pathDefinitions.toString(),
                        SERVICE_PORT_PLACEHOLDER, String.valueOf(PortManager.getInstance().getServicePort(MockType.EXTERNAL_SERVICES.getServiceName()))
                )
        );

        FileUtility.generateFileFromTemplate(
                Paths.TEMPLATE_ES_PATH + PACKAGE_JSON_FILE_TEMPLATE,
                Paths.ES_FOLDER,
                PACKAGE_JSON_FILE,
                ImmutableMap.of(DEPENDENCY_DEFINITIONS_PLACEHOLDER, dependencies)
        );

        FileUtility.copyFile(
                Paths.TEMPLATE_ES_PATH + GITIGNORE_FILE,
                Paths.ES_FOLDER,
                GITIGNORE_FILE
        );
    }

    /**
     * Get Dependency configuration string in (name : version) format
     * @param userMocksConfiguration
     * @return package dependencies as string
     */
    private String getDependencies(UserMocksConfiguration userMocksConfiguration) {
        StringBuilder dependencyDefinitions = new StringBuilder();
        for (DependencyConfiguration dependencyConfiguration
                : userMocksConfiguration.getExternalServices().getDependencies()) {
            dependencyDefinitions
                    .append(",\n")
                    .append(String.format(TEMPLATE_DEPENDENCY_DEFINITION,
                            dependencyConfiguration.getName(),
                            dependencyConfiguration.getVersion()));
        }
        return dependencyDefinitions.toString();
    }
}
