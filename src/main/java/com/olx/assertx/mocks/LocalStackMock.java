package com.olx.assertx.mocks;

import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.LocalStack;
import com.olx.assertx.mocks.model.MockType;
import com.olx.assertx.mocks.model.Networks;
import com.olx.assertx.mocks.model.Service1Net;
import com.olx.assertx.utils.FileUtility;
import com.olx.assertx.utils.PortManager;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocalStackMock implements BaseMock {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalStackMock.class);
    private static final String VOLUME_DATA_PATH_FORMAT = "./%s/data:/%s";
    // LocalStack < 2.0 ran init scripts from /docker-entrypoint-initaws.d; 2.0+ only runs them from
    // /etc/localstack/init/ready.d. Mount dataDir at both so any image version picks the scripts up.
    private static final String LEGACY_INIT_DIR = "docker-entrypoint-initaws.d";
    private static final String READY_INIT_DIR = "etc/localstack/init/ready.d";
    private static final String DEFAULT_HOSTNAME = "0.0.0.0";

    @Override
    public boolean isEnabled(UserMocksConfiguration userMocksConfiguration) {
        return userMocksConfiguration.getLocalstack().isEnabled();
    }

    @Override
    public LocalStack map(FrameworkConfiguration frameworkConfiguration, UserMocksConfiguration userMocksConfiguration, String serviceName) {
        int port = PortManager.getInstance().getServicePort(serviceName);

        LOGGER.info("Map {} mock on port={}", getMockType(), port);

        return LocalStack
                .builder()
                .image(getDockerImage(frameworkConfiguration, userMocksConfiguration))
                .containerName(serviceName)
                .environment(Arrays.asList(
                        String.format(ENV_FORMAT, "SERVICES", getAWSServices(frameworkConfiguration, userMocksConfiguration)),
                        String.format(ENV_FORMAT, "DEFAULT_REGION", getAWSRegion(frameworkConfiguration, userMocksConfiguration)),
                        String.format(ENV_FORMAT, "HOSTNAME", DEFAULT_HOSTNAME)
                        ))
                .volumes(getVolumes(userMocksConfiguration))
                .ports(Arrays.asList(String.format(PORT_MAPPING_FORMAT, port, port)))
                // Same network as every other mock, so Toxiproxy can reach it as upstream `localstack`
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder().build())
                        .build())
                .build();
    }

    @Override
    public MockType getMockType() {
        return MockType.LOCALSTACK;
    }

    @Override
    public String getDockerImage(FrameworkConfiguration frameworkConfiguration, UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getLocalstack().getImage() != null) {
            return userMocksConfiguration.getLocalstack().getImage();
        }
        return frameworkConfiguration.getServiceDefaults().getLocalstack().getImage();
    }

    @Override
    public int getServicePort(FrameworkConfiguration frameworkConfiguration, UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getLocalstack().getPort() > 0) {
            return userMocksConfiguration.getLocalstack().getPort();
        }
        return frameworkConfiguration.getServiceDefaults().getLocalstack().getPort();
    }

    @Override
    public void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) throws IOException {
        LOGGER.info("Copy startup files for mock={}", getMockType());

        // Check if any warm up script needs to be copied
        if (!StringUtils.isEmpty(userMocksConfiguration.getLocalstack().getDataDir())) {
            FileUtility.copyDir(
                    userMocksConfiguration.getLocalstack().getDataDir(),
                    String.format(TARGET_DOCKERFILE_PATH_FORMAT, getMockType().getServiceName()) + "/data"
            );
            // LocalStack only runs init scripts that are executable; copying drops the permission bits
            makeShellScriptsExecutable(
                    String.format(TARGET_DOCKERFILE_PATH_FORMAT, getMockType().getServiceName()) + "/data");
        }
    }

    private String getAWSServices(FrameworkConfiguration frameworkConfiguration, UserMocksConfiguration userMocksConfiguration) {
        if (!userMocksConfiguration.getLocalstack().getServices().isEmpty()) {
            return StringUtils.join(userMocksConfiguration.getLocalstack().getServices(), ",");
        }
        return StringUtils.join(frameworkConfiguration.getServiceDefaults().getLocalstack().getServices(), ",");
    }

    private String getAWSRegion(FrameworkConfiguration frameworkConfiguration, UserMocksConfiguration userMocksConfiguration) {
        if (StringUtils.isNotBlank(userMocksConfiguration.getLocalstack().getRegion())) {
            return userMocksConfiguration.getLocalstack().getRegion();
        }
        return frameworkConfiguration.getServiceDefaults().getLocalstack().getRegion();
    }

    private List<String> getVolumes(UserMocksConfiguration userMocksConfiguration) {
        List<String> volumes = new ArrayList<>();
        if (!StringUtils.isEmpty(userMocksConfiguration.getLocalstack().getDataDir())) {
            for (String initDir : List.of(LEGACY_INIT_DIR, READY_INIT_DIR)) {
                volumes.add(String.format(VOLUME_DATA_PATH_FORMAT, getMockType().getServiceName(), initDir));
            }
        }
        return volumes;
    }

    private void makeShellScriptsExecutable(String dir) throws IOException {
        try (java.util.stream.Stream<java.nio.file.Path> files = java.nio.file.Files.walk(java.nio.file.Paths.get(dir))) {
            files.filter(f -> f.toString().endsWith(".sh"))
                    .forEach(f -> f.toFile().setExecutable(true, false));
        }
    }
}
