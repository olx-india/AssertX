package com.olx.assertx.mocks;

import com.olx.assertx.client.ToxiProxyClient;
import com.olx.assertx.configuration.FrameworkConfiguration;
import com.olx.assertx.configuration.GlobalMocksConfiguration;
import com.olx.assertx.configuration.RouteConfiguration;
import com.olx.assertx.configuration.UserMocksConfiguration;
import com.olx.assertx.mocks.model.*;
import com.olx.assertx.utils.FileUtility;
import com.olx.assertx.utils.Paths;
import com.olx.assertx.utils.PortManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ToxiProxyMock implements BaseMock {
    private static final Logger LOGGER = LoggerFactory.getLogger(ToxiProxyMock.class);
    private static final String CONFIG_FILE = "config.json";

    @Override
    public boolean isEnabled(UserMocksConfiguration userMocksConfiguration) {
        return userMocksConfiguration.getToxiproxy().isEnabled();
    }

    @Override
    public BaseService map(FrameworkConfiguration frameworkConfiguration,
                           UserMocksConfiguration userMocksConfiguration,
                           String serviceName) {
        int toxiProxyPort = PortManager.getInstance().getServicePort(serviceName);

        LOGGER.info("Map {} mock on port={}", getMockType(), toxiProxyPort);

        List<String> allPortMappings = new java.util.ArrayList<>();
        allPortMappings.add(String.format(PORT_MAPPING_FORMAT, toxiProxyPort, toxiProxyPort));
        allPortMappings.addAll(getExternalServicesPortMappings());

        return ToxiProxy.builder()
                .build(serviceName)
                .containerName(serviceName)
                .restart("always")
                .ports(allPortMappings)
                .networks(Networks.builder()
                        .service1NetName(Service1Net.builder().build())
                        .build())
                .healthcheck(Healthcheck.builder()
                        .test(List.of("CMD", "nc", "-vz", "localhost", String.valueOf(toxiProxyPort)))
                        .interval("2s")
                        .retries(3)
                        .timeout("5s")
                        .build())
                .build();
    }

    @Override
    public MockType getMockType() {
        return MockType.TOXIPROXY;
    }

    @Override
    public String getDockerImage(FrameworkConfiguration frameworkConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) {
        return null;
    }

    @Override
    public int getServicePort(FrameworkConfiguration frameworkConfiguration,
                              UserMocksConfiguration userMocksConfiguration) {
        if (userMocksConfiguration.getToxiproxy().getPort() > 0) {
            return userMocksConfiguration.getToxiproxy().getPort();
        }
        return frameworkConfiguration.getServiceDefaults().getToxiproxy().getPort();
    }

    @Override
    public void copyStartupFiles(GlobalMocksConfiguration globalMocksConfiguration,
                                 UserMocksConfiguration userMocksConfiguration) throws IOException {
        LOGGER.info("Copy startup files for mock={}", getMockType());
        configureAndLoadDockerFile();

        LOGGER.info("Build ToxiProxy config={} at path={}", CONFIG_FILE, Paths.TOXI_PROXY_FOLDER);
        Set<String> externalServices = userMocksConfiguration.getExternalServices().getUserSpecified().getRoutes()
                .stream()
                .map(RouteConfiguration::getType)
                .collect(Collectors.toSet());
        externalServices.addAll(userMocksConfiguration.getExternalServices().getGlobal());

        List<ToxiProxyConfigurations> toxiProxyConfig = PortManager.getInstance().getToxiProxyPortMap().keySet()
                .stream()
                .map(service -> ToxiProxyConfigurations.builder()
                        .name(service)
                        .listen(String.format(PORT_MAPPING_FORMAT, ToxiProxyClient.TOXI_PROXY_HOST,
                                        PortManager.getInstance().getToxiProxyPortMap().get(service)))
                        .upstream(externalServices.contains(service) ?
                                String.format(PORT_MAPPING_FORMAT, ToxiProxyClient.TOXI_PROXY_HOST,
                                        PortManager.getInstance().getToxiProxyPortMap().get(MockType.EXTERNAL_SERVICES.getServiceName())) :
                                String.format(PORT_MAPPING_FORMAT, service,
                                        PortManager.getInstance().getServicePort(service)))
                        .build())
                .collect(Collectors.toList());

        FileUtility.writeFile(Paths.TOXI_PROXY_FOLDER, CONFIG_FILE, toxiProxyConfig);
    }

    private List<String> getExternalServicesPortMappings() {
        LOGGER.info("Get port mappings for all external services");
        return PortManager.getInstance().getToxiProxyPortMap().keySet().stream()
                .map(service -> {
                    int proxyPort = PortManager.getInstance().getToxiProxyPortMap().get(service);
                    return String.format(PORT_MAPPING_FORMAT, proxyPort, proxyPort);
                }).collect(Collectors.toList());
    }
}
