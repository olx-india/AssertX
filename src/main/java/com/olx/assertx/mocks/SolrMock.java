package com.olx.assertx.mocks;

import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.SolrCollectionConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.*;
import com.olx.assertx.utils.FileUtility;
import com.olx.assertx.utils.PortManager;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class SolrMock implements BaseMock {
    private final String CONFIG_SET_PATH = "/tmp/config-sets";
    private final String CREATE_COLLECTION_CMD = "precreate-core %s "+ CONFIG_SET_PATH + "/%s ;";
    private final String VOLUME_COLLECTIONS_PATH_FORMAT = "./%s/collections:" + CONFIG_SET_PATH;

    @Override
    public boolean isEnabled(UserMocksConfiguration userMocksConfiguration) {
        return userMocksConfiguration.getSolr().isEnabled();
    }

    @Override
    public BaseService map(FrameworkConfiguration frameworkConfiguration,
                           UserMocksConfiguration userMocksConfiguration,
                           String serviceName) {
        int port = PortManager.getInstance().getServicePort(serviceName);
        LOGGER.info("Map {} mock on port={}", getMockType(), port);

        List<String> volumes = new ArrayList<>();
        if (!StringUtils.isEmpty(userMocksConfiguration.getSolr().getCollectionsDir())) {
            volumes.add(
                    String.format(
                            VOLUME_COLLECTIONS_PATH_FORMAT,
                            getMockType().getServiceName()
                    )
            );
        }

        return Solr.builder()
                .image(getDockerImage(frameworkConfiguration, userMocksConfiguration))
                .containerName(serviceName)
                .ports(Collections.singletonList(String.format(PORT_MAPPING_FORMAT, port, port)))
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder().build())
                        .build())
                .volumes(volumes)
                .entrypoint(Arrays.asList("sh", "-c", "docker-entrypoint.sh " + getEntrypointCommand(userMocksConfiguration.getSolr().getCollections()) + "solr-foreground"))
                .healthcheck(Healthcheck.builder()
                        .test(List.of("CMD-SHELL", "curl -sf http://localhost:" + port + "/solr/" + getPrimaryCollection(userMocksConfiguration.getSolr().getCollections()) + "/admin/ping?wt=json | python -c \"import sys, json; print json.load(sys.stdin)['status']\" | grep -iq \"ok\" || exit 1"))
                        .interval("10s")
                        .retries(3)
                        .timeout("50s")
                        .build())
                .build();
    }

    @Override
    public MockType getMockType() {
        return MockType.SOLR;
    }

    @Override
    public String getDockerImage(FrameworkConfiguration frameworkConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getSolr().getImage() != null) {
            return userMocksConfiguration.getSolr().getImage();
        }
        return frameworkConfiguration.getServiceDefaults().getSolr().getImage();
    }

    @Override
    public int getServicePort(FrameworkConfiguration frameworkConfiguration,
                              UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getSolr().getPort() > 0) {
            return userMocksConfiguration.getSolr().getPort();
        }
        return frameworkConfiguration.getServiceDefaults().getSolr().getPort();
    }

    @Override
    public void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) throws IOException {
        LOGGER.info("Copy startup files for mock={}", getMockType());

        // Copy collections directory
        if (!StringUtils.isEmpty(userMocksConfiguration.getSolr().getCollectionsDir())) {
            FileUtility.copyDir(
                    userMocksConfiguration.getSolr().getCollectionsDir(),
                    String.format(TARGET_DOCKERFILE_PATH_FORMAT, getMockType().getServiceName()) + "/collections"
            );
        }
    }

    private String getPrimaryCollection(List<SolrCollectionConfiguration> collections) {
        Optional<SolrCollectionConfiguration> primaryCollection = collections.stream()
                .filter(SolrCollectionConfiguration::isPrimary)
                .findFirst();
        if (primaryCollection.isEmpty()) {
            primaryCollection = collections.stream().findFirst();
        }
        if (primaryCollection.isEmpty()) {
            throw new RuntimeException("No primary collection set for Solr in config");
        }
        return primaryCollection.get().getName();
    }

    private String getEntrypointCommand(List<SolrCollectionConfiguration> collections) {
        StringBuilder entrypointCommand = new StringBuilder();
        for (SolrCollectionConfiguration collection : collections) {
            entrypointCommand.append(String.format(CREATE_COLLECTION_CMD, collection.getName(), collection.getConfigPath()));
        }
        return entrypointCommand.toString();
    }
}
