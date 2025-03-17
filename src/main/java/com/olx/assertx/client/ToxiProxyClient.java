package com.olx.assertx.client;

import com.olx.assertx.mocks.model.MockType;
import com.olx.assertx.service.model.PropertyKey;
import com.olx.assertx.utils.PortManager;
import eu.rekawek.toxiproxy.ToxiproxyClient;

public class ToxiProxyClient {
    public static final String TOXI_PROXY_HOST = "0.0.0.0";

    private ToxiProxyClient() {}

    private static class BillPughSingleton {
        private static final ToxiproxyClient INSTANCE = new ToxiproxyClient(System.getProperty(PropertyKey.DOCKER_HOST.getValue()),
                PortManager.getInstance().getServicePort(MockType.TOXIPROXY.getServiceName()));
    }

    public static ToxiproxyClient getInstance() {
        return BillPughSingleton.INSTANCE;
    }
}
