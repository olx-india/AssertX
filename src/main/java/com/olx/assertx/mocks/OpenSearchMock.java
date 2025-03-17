package com.olx.assertx.mocks;

import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.configuration.VolumeConfiguration;
import com.olx.assertx.mocks.model.*;
import com.olx.assertx.utils.FileUtility;
import com.olx.assertx.utils.Paths;
import com.olx.assertx.utils.PortManager;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenSearchMock implements BaseMock {
    @Override
    public boolean isEnabled(UserMocksConfiguration userMocksConfiguration) {
        return userMocksConfiguration.getOpensearch().isEnabled();
    }

    @Override
    public BaseService map(FrameworkConfiguration frameworkConfiguration,
                           UserMocksConfiguration userMocksConfiguration,
                           String serviceName) {
        int port = PortManager.getInstance().getServicePort(serviceName);
        LOGGER.info("Map {} mock on port={}", getMockType(), port);

        List<String> volumes = new ArrayList<>();
        for (VolumeConfiguration volumeConfig : userMocksConfiguration.getOpensearch().getVolumes()) {
            final String srcPath = "./" + getMockType().getServiceName() + "/" + volumeConfig.getDestFolder();
            final String destPath = volumeConfig.getDestPath() + "/" + volumeConfig.getDestFolder();
            volumes.add(srcPath + ":" + destPath);
        }

        List<String> ports = new ArrayList<>();
        ports.add(port + ":" + port);
        for (Integer extraPort : userMocksConfiguration.getOpensearch().getExtraPorts()) {
            ports.add(extraPort + ":" + extraPort);
        }

        return OpenSearch.builder()
                .image(getDockerImage(frameworkConfiguration, userMocksConfiguration))
                .containerName(serviceName)
                .ports(ports)
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder().build())
                        .build())
                .environment(List.of(
                        "discovery.type=single-node",
                        "bootstrap.memory_lock=true",
                        "OPENSEARCH_JAVA_OPTS=-Xms512m -Xmx512m",
                        "DISABLE_INSTALL_DEMO_CONFIG=true",
                        "DISABLE_SECURITY_PLUGIN=true"))
                .volumes(volumes)
                .healthcheck(Healthcheck.builder()
                        .test(List.of("CMD-SHELL",
                                "curl -f -X GET 'localhost:" + port + "/_cluster/health?wait_for_status=green&timeout=1s' || exit 1"))
                        .interval("10s")
                        .timeout("5s")
                        .retries(3)
                        .build())
                .build();
    }

    @Override
    public MockType getMockType() {
        return MockType.OPENSEARCH;
    }

    @Override
    public String getDockerImage(FrameworkConfiguration frameworkConfiguration, UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getOpensearch().getImage() != null) {
            return userMocksConfiguration.getOpensearch().getImage();
        }
        return frameworkConfiguration.getServiceDefaults().getOpensearch().getImage();
    }

    @Override
    public int getServicePort(FrameworkConfiguration frameworkConfiguration, UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getOpensearch().getPort() > 0) {
            return userMocksConfiguration.getOpensearch().getPort();
        }
        return frameworkConfiguration.getServiceDefaults().getOpensearch().getPort();
    }

    @Override
    public void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) throws IOException {
        LOGGER.info("Copy startup files for mock={}", getMockType().getServiceName());

        for (VolumeConfiguration volumeConfig : userMocksConfiguration.getOpensearch().getVolumes()) {
            final String srcPath = volumeConfig.getSrcPath();
            final String destPath = Paths.MOCKS_FOLDER + "/" + getMockType().getServiceName()
                    + "/" + volumeConfig.getDestFolder();
            if (!StringUtils.isEmpty(srcPath)) {
                FileUtility.copyDir(srcPath, destPath);
            }
        }
    }
}
