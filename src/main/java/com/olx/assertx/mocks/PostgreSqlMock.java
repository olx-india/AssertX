package com.olx.assertx.mocks;

import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.*;
import com.olx.assertx.utils.FileUtility;
import com.olx.assertx.utils.PortManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PostgreSqlMock implements BaseMock {
    private static final Logger LOGGER = LoggerFactory.getLogger(PostgreSqlMock.class);
    private static final String VOLUME_DATA_PATH_FORMAT = "./%s/data:/%s";
    private static final String DATA_FILE_FORMAT = "docker-entrypoint-initdb.d";
    private static final String DEFAULT_DB_NAME = "test";
    private static final String DEFAULT_DB_USER = "postgres";
    private static final String DEFAULT_DB_PASS = "postgres";

    @Override
    public boolean isEnabled(UserMocksConfiguration userMocksConfiguration) {
        return userMocksConfiguration.getPostgresql().isEnabled();
    }

    @Override
    public BaseService map(FrameworkConfiguration frameworkConfiguration,
                           UserMocksConfiguration userMocksConfiguration,
                           String serviceName) {
        int port = PortManager.getInstance().getServicePort(serviceName);
        LOGGER.info("Map {} mock on port={}", getMockType(), port);

        List<String> volumes = new ArrayList<>();

        if (!StringUtils.isEmpty(userMocksConfiguration.getPostgresql().getDataDir())) {
            volumes.add(
                    String.format(
                            VOLUME_DATA_PATH_FORMAT,
                            getMockType().getServiceName(),
                            DATA_FILE_FORMAT
                    )
            );
        }

        return PostgreSql.builder()
                .image(getDockerImage(frameworkConfiguration, userMocksConfiguration))
                .containerName(serviceName)
                .environment(Arrays.asList(
                        String.format(ENV_FORMAT, "POSTGRES_DB", DEFAULT_DB_NAME),
                        String.format(ENV_FORMAT, "POSTGRES_USER", DEFAULT_DB_USER),
                        String.format(ENV_FORMAT, "POSTGRES_PASSWORD", DEFAULT_DB_PASS)
                ))
                .ports(Collections.singletonList(String.format(PORT_MAPPING_FORMAT, port,
                        frameworkConfiguration.getServiceDefaults().getPostgresql().getPort())))
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder().build())
                        .build())
                .volumes(volumes)
                .healthcheck(Healthcheck.builder()
                        .test(List.of("CMD", "pg_isready", "-U", DEFAULT_DB_USER, "-d", DEFAULT_DB_NAME))
                        .interval("2s")
                        .retries(10)
                        .timeout("20s")
                        .build())
                .build();
    }

    @Override
    public MockType getMockType() {
        return MockType.POSTGRESQL;
    }

    @Override
    public String getDockerImage(FrameworkConfiguration frameworkConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getPostgresql().getImage() != null) {
            return userMocksConfiguration.getPostgresql().getImage();
        }
        return frameworkConfiguration.getServiceDefaults().getPostgresql().getImage();
    }

    @Override
    public int getServicePort(FrameworkConfiguration frameworkConfiguration,
                              UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getPostgresql().getPort() > 0) {
            return userMocksConfiguration.getPostgresql().getPort();
        }
        return frameworkConfiguration.getServiceDefaults().getPostgresql().getPort();
    }

    @Override
    public void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) throws IOException {
        LOGGER.info("Copy startup files for mock={}", getMockType());

        // Check if any warm up script needs to be copied
        if (!StringUtils.isEmpty(userMocksConfiguration.getPostgresql().getDataDir())) {
            FileUtility.copyDir(
                    userMocksConfiguration.getPostgresql().getDataDir(),
                    String.format(TARGET_DOCKERFILE_PATH_FORMAT, getMockType().getServiceName()) + "/data"
            );
        }
    }
}
