package com.olx.assertx.mocks.builder;

import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.BaseCustomMock;
import com.olx.assertx.mocks.BaseMock;
import com.olx.assertx.mocks.factory.MockFactory;
import com.olx.assertx.mocks.model.*;
import com.olx.assertx.utils.FileUtility;
import com.olx.assertx.utils.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class DockerComposeBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DockerComposeBuilder.class);
    public static final String DOCKER_COMPOSE_FILENAME = "docker-compose.json";
    /**
     * Every container AssertX starts is named {@code assertx-<mock>}. Containers are created with
     * removeConflictingContainersOnStartup, so plain names like {@code redis} or {@code mysql} would make
     * AssertX delete unrelated containers that happen to share them. Compose service names (and so the DNS
     * names mocks use to reach each other, e.g. {@code kafka}, {@code zookeeper}) are unchanged.
     */
    public static final String CONTAINER_NAME_PREFIX = "assertx-";

    public static void build(UserMocksConfiguration userMocksConfiguration,
                             FrameworkConfiguration frameworkConfiguration) throws IOException {

        LOGGER.info("Building docker compose");

        FileUtility.deleteDirectory(Paths.BASE_IT_FOLDER);

        // Build all available mocks
        Services services = new Services();
        for (MockType mockType : frameworkConfiguration.getAvailableMocks()) {
            BaseMock mock = MockFactory.getMock(mockType);
            mock.build(frameworkConfiguration, userMocksConfiguration, services);
        }

        // Build all custom mocks
        for (String customMockClass : userMocksConfiguration.getCustom()) {
            try {
                BaseCustomMock baseCustomMock = (BaseCustomMock) Class.forName(customMockClass).getConstructor().newInstance();
                baseCustomMock.build(userMocksConfiguration, services);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                    InvocationTargetException ex) {
                LOGGER.error("Unable to load mock={} due to exception={}", customMockClass, ex);
                return;
            }
        }

        // Build toxi proxy mock
        BaseMock mock = MockFactory.getMock(MockType.TOXIPROXY);
        mock.build(frameworkConfiguration, userMocksConfiguration, services);

        prefixContainerNames(services);

        // Build docker compose
        DockerCompose dockerCompose = DockerCompose.builder()
                .services(services)
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder()
                                .build())
                        .build())
                .build();

        LOGGER.info("Write {} file to path={}", DOCKER_COMPOSE_FILENAME, Paths.MOCKS_FOLDER);
        FileUtility.writeFile(Paths.MOCKS_FOLDER, DOCKER_COMPOSE_FILENAME, dockerCompose);
    }

    private static void prefixContainerNames(Services services) {
        services.getDynamicAttributes().forEach((serviceName, service) -> {
            if (service instanceof BaseService baseService) {
                baseService.setContainerName(CONTAINER_NAME_PREFIX + serviceName);
            }
        });
    }

}
