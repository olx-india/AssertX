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

public class MysqlMock implements BaseMock {
    private static final Logger LOGGER = LoggerFactory.getLogger(MysqlMock.class);
    private static final String VOLUME_DATA_PATH_FORMAT = "./%s/data:/%s";
    private static final String DATA_FILE_FORMAT = "docker-entrypoint-initdb.d";
    private static final String DEFAULT_DB_NAME = "test";
    private static final String DEFAULT_DB_PASS = "1234";

    @Override
    public boolean isEnabled(UserMocksConfiguration userMocksConfiguration) {
        return userMocksConfiguration.getMysql().isEnabled();
    }

    @Override
    public BaseService map(FrameworkConfiguration frameworkConfiguration,
                           UserMocksConfiguration userMocksConfiguration,
                           String serviceName) {
        int port = PortManager.getInstance().getServicePort(serviceName);
        LOGGER.info("Map {} mock on port={}", getMockType(), port);
        String database = StringUtils.isEmpty(userMocksConfiguration.getMysql().getDb()) ?
                DEFAULT_DB_NAME : userMocksConfiguration.getMysql().getDb();

        List<String> volumes = new ArrayList<>();

        if (!StringUtils.isEmpty(userMocksConfiguration.getMysql().getDataDir())) {
            volumes.add(
                    String.format(
                            VOLUME_DATA_PATH_FORMAT,
                            getMockType().getServiceName(),
                            DATA_FILE_FORMAT
                    )
            );
        }

        return Mysql.builder()
                .image(getDockerImage(frameworkConfiguration, userMocksConfiguration))
                .containerName(serviceName)
                .environment(Arrays.asList(
                        String.format(ENV_FORMAT, "MYSQL_DATABASE", database),
                        String.format(ENV_FORMAT, "MYSQL_ROOT_PASSWORD", DEFAULT_DB_PASS),
                        String.format(ENV_FORMAT, "MYSQL_TCP_PORT", port)
                        ))
                .ports(Collections.singletonList(String.format(PORT_MAPPING_FORMAT, port, port)))
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder().build())
                        .build())
                .volumes(volumes)
                .healthcheck(Healthcheck.builder()
                        .test(List.of("CMD", "mysqladmin", "ping", "-h", "localhost"))
                        .interval("2s")
                        .retries(10)
                        .timeout("20s")
                        .build())
                .build();
    }

    @Override
    public MockType getMockType() {
        return MockType.MYSQL;
    }

    @Override
    public String getDockerImage(FrameworkConfiguration frameworkConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getMysql().getImage() != null) {
            return userMocksConfiguration.getMysql().getImage();
        }
        return frameworkConfiguration.getServiceDefaults().getMysql().getImage();
    }

    @Override
    public int getServicePort(FrameworkConfiguration frameworkConfiguration,
                              UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getMysql().getPort() > 0) {
            return userMocksConfiguration.getMysql().getPort();
        }
        return frameworkConfiguration.getServiceDefaults().getMysql().getPort();
    }

    @Override
    public void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) throws IOException {
        LOGGER.info("Copy startup files for mock={}", getMockType());

        // Check if any warm up script needs to be copied
        if (!StringUtils.isEmpty(userMocksConfiguration.getMysql().getDataDir())) {
            FileUtility.copyDir(
                    userMocksConfiguration.getMysql().getDataDir(),
                    String.format(TARGET_DOCKERFILE_PATH_FORMAT, getMockType().getServiceName()) + "/data"
            );
        }
    }
}
