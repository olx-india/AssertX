package com.olx.assertx.mocks;

import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.*;
import com.olx.assertx.service.model.PropertyKey;
import com.olx.assertx.utils.PortManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KafkaMock implements BaseMock {

    @Override
    public boolean isEnabled(UserMocksConfiguration userMocksConfiguration) {
        return userMocksConfiguration.getKafka().isEnabled()
                && userMocksConfiguration.getZookeeper().isEnabled();
    }

    @Override
    public BaseService map(FrameworkConfiguration frameworkConfiguration,
                           UserMocksConfiguration userMocksConfiguration,
                           String serviceName) {
        int port = PortManager.getInstance().getServicePort(serviceName);
        LOGGER.info("Map {} mock on port={}", getMockType(), port);

        String host = System.getProperty(PropertyKey.DOCKER_HOST.getValue());
        if (host.equalsIgnoreCase("0.0.0.0")) {
            host = "localhost";
        }

        return Kafka.builder()
                .image(getDockerImage(frameworkConfiguration, userMocksConfiguration))
                .containerName(serviceName)
                .environment(Arrays.asList(
                        String.format(ENV_FORMAT, "KAFKA_LISTENERS",
                                String.format("EXTERNAL_SAME_HOST://:%s,INTERNAL://:9092", port)),
                        String.format(ENV_FORMAT, "KAFKA_ADVERTISED_LISTENERS",
                                String.format("INTERNAL://kafka:9092,EXTERNAL_SAME_HOST://%s:%s", host, port)),
                        String.format(ENV_FORMAT, "KAFKA_LISTENER_SECURITY_PROTOCOL_MAP",
                                "INTERNAL:PLAINTEXT,EXTERNAL_SAME_HOST:PLAINTEXT"),
                        String.format(ENV_FORMAT, "KAFKA_INTER_BROKER_LISTENER_NAME",
                                "INTERNAL"),
                        String.format(ENV_FORMAT, "KAFKA_ZOOKEEPER_CONNECT",
                                "zookeeper:" + PortManager.getInstance().getServicePort(MockType.ZOOKEEPER.getServiceName())),
                        String.format(ENV_FORMAT,
                                "KAFKA_CREATE_TOPICS", getKafkaTopics(userMocksConfiguration.getKafka().getTopics()))
                        ))
                .ports(Collections.singletonList(String.format(PORT_MAPPING_FORMAT, port, port)))
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder().build())
                        .build())
                .volumes(Collections.singletonList("/var/run/docker.sock:/var/run/docker.sock"))
                .healthcheck(Healthcheck.builder()
                        .test(Arrays.asList("CMD", "nc", "-vz", "localhost", String.valueOf(port)))
                        .interval("5s")
                        .retries(3)
                        .timeout("50s")
                        .build())
                .build();
    }

    @Override
    public MockType getMockType() {
        return MockType.KAFKA;
    }

    @Override
    public String getDockerImage(FrameworkConfiguration frameworkConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getKafka().getImage() != null) {
            return userMocksConfiguration.getKafka().getImage();
        }
        return frameworkConfiguration.getServiceDefaults().getKafka().getImage();
    }

    @Override
    public int getServicePort(FrameworkConfiguration frameworkConfiguration,
                              UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getKafka().getPort() > 0) {
            return userMocksConfiguration.getKafka().getPort();
        }
        return frameworkConfiguration.getServiceDefaults().getKafka().getPort();
    }

    @Override
    public void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) {
    }

    private String getKafkaTopics(List<String> topics) {
        StringBuilder topicsStr = new StringBuilder();
        for (String topic : topics) {
            if (topicsStr.length() > 0) {
                topicsStr.append(",");
            }
            topicsStr.append(String.format("%s:1:1", topic));
        }
        return topicsStr.toString();
    }
}
