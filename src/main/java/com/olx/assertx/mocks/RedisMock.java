package com.olx.assertx.mocks;

import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.Healthcheck;
import com.olx.assertx.mocks.model.MockType;
import com.olx.assertx.mocks.model.Networks;
import com.olx.assertx.mocks.model.Redis;
import com.olx.assertx.mocks.model.Service1Net;
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

public class RedisMock implements BaseMock {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisMock.class);
    private static final String VOLUME_DATA_PATH_FORMAT = "./%s/data/%s:/%s";
    private static final String REDIS_CONF = "redis.conf";
    private static final String REDIS_CONF_PATH = "/" + REDIS_CONF;
    private static final String REDIS_SERVER_COMMAND = "redis-server";

    @Override
    public boolean isEnabled(UserMocksConfiguration userMocksConfiguration) {
        return userMocksConfiguration.getRedis().isEnabled();
    }

    @Override
    public Redis map(FrameworkConfiguration frameworkConfiguration,
                     UserMocksConfiguration userMocksConfiguration,
                     String serviceName) {
        int port = PortManager.getInstance().getServicePort(serviceName);

        LOGGER.info("Map {} mock on port={}", getMockType(), port);

        List<String> volumes = new ArrayList<>();
        List<String> command = new ArrayList<>();

        if (StringUtils.isNotBlank(userMocksConfiguration.getRedis().getConfigFilePath())) {
            // mount redis conf file
            volumes.add(
                    String.format(
                            VOLUME_DATA_PATH_FORMAT,
                            getMockType().getServiceName(),
                            REDIS_CONF,
                            REDIS_CONF
                    )
            );

            // Add command to run redis-server using conf file
            command.addAll(
                    Arrays.asList(
                            REDIS_SERVER_COMMAND,
                            REDIS_CONF_PATH
                    )
            );
        }

        return Redis.builder()
                .image(getDockerImage(frameworkConfiguration, userMocksConfiguration))
                .containerName(serviceName)
                .user("root")
                .environment(Arrays.asList(
                        String.format(ENV_FORMAT, "REDIS_APPENDFSYNC", "always"),
                        String.format(ENV_FORMAT, "ALLOW_EMPTY_PASSWORD", "yes"),
                        String.format(ENV_FORMAT, "DISABLE_COMMANDS", "FLUSHDB,FLUSHALL")
                        ))
                .ports(Collections.singletonList(String.format(PORT_MAPPING_FORMAT, port, port)))
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder().build())
                        .build())
                .healthcheck(Healthcheck.builder()
                        .test(List.of("CMD", "redis-cli", "ping"))
                        .interval("2s")
                        .retries(3)
                        .timeout("5s")
                        .build())
                .volumes(volumes)
                .command(command)
                .build();
    }

    @Override
    public MockType getMockType() {
        return MockType.REDIS;
    }

    @Override
    public String getDockerImage(FrameworkConfiguration frameworkConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getRedis().getImage() != null) {
            return userMocksConfiguration.getRedis().getImage();
        }
        return frameworkConfiguration.getServiceDefaults().getRedis().getImage();
    }

    @Override
    public int getServicePort(FrameworkConfiguration frameworkConfiguration,
                              UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getRedis().getPort() > 0) {
            return userMocksConfiguration.getRedis().getPort();
        }
        return frameworkConfiguration.getServiceDefaults().getRedis().getPort();
    }

    @Override
    public void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) throws IOException {
        LOGGER.info("Copy startup files for mock={}", getMockType());

        // Check if any config file needs to be copied
        if (StringUtils.isNotBlank(userMocksConfiguration.getRedis().getConfigFilePath())) {
            FileUtility.copyFile(
                    userMocksConfiguration.getRedis().getConfigFilePath(),
                    String.format(TARGET_DOCKERFILE_PATH_FORMAT, getMockType().getServiceName()) + "/data",
                    REDIS_CONF
            );
        }
    }
}
