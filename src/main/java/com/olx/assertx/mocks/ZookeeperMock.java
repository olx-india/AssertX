package com.olx.assertx.mocks;

import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.*;
import com.olx.assertx.utils.PortManager;

import java.util.Collections;
import java.util.List;

public class ZookeeperMock implements BaseMock {
    @Override
    public boolean isEnabled(UserMocksConfiguration userMocksConfiguration) {
        return userMocksConfiguration.getZookeeper().isEnabled();
    }

    @Override
    public BaseService map(FrameworkConfiguration frameworkConfiguration,
                           UserMocksConfiguration userMocksConfiguration,
                           String serviceName) {
        int port = PortManager.getInstance().getServicePort(serviceName);
        LOGGER.info("Map {} mock on port={}", getMockType(), port);

        return Zookeeper.builder()
                .image(getDockerImage(frameworkConfiguration, userMocksConfiguration))
                .containerName(serviceName)
                .ports(Collections.singletonList(String.format(PORT_MAPPING_FORMAT, port, port)))
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder().build())
                        .build())
                .healthcheck(Healthcheck.builder()
                        .test(List.of("CMD", "nc", "-vz", "localhost", String.valueOf(port)))
                        .interval("5s")
                        .retries(3)
                        .timeout("50s")
                        .build())
                .build();
    }

    @Override
    public MockType getMockType() {
        return MockType.ZOOKEEPER;
    }

    @Override
    public String getDockerImage(FrameworkConfiguration frameworkConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getZookeeper().getImage() != null) {
            return userMocksConfiguration.getZookeeper().getImage();
        }
        return frameworkConfiguration.getServiceDefaults().getZookeeper().getImage();
    }

    @Override
    public int getServicePort(FrameworkConfiguration frameworkConfiguration,
                              UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getZookeeper().getPort() > 0) {
            return userMocksConfiguration.getZookeeper().getPort();
        }
        return frameworkConfiguration.getServiceDefaults().getZookeeper().getPort();
    }

    @Override
    public void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) {
    }
}
