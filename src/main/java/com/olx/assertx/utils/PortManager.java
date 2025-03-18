package com.olx.assertx.utils;

import org.springframework.cloud.test.TestSocketUtils;

import java.util.HashMap;
import java.util.Map;

public class PortManager {
    private static final Map<String, Integer> servicePortMap = new HashMap<>();
    private static final Map<String, Integer> toxiProxyPortMap = new HashMap<>();
    private static final PortManager portManager = new PortManager();
    private static int proxyPortCounter = 31274;

    public static PortManager getInstance() {
        return portManager;
    }

    public int getServicePort(String serviceName) {
        if (!servicePortMap.containsKey(serviceName)) {
            int port = TestSocketUtils.findAvailableTcpPort();
            servicePortMap.put(serviceName, port);
        }
        return servicePortMap.get(serviceName);
    }

    public Map<String, Integer> getToxiProxyPortMap() {
        return toxiProxyPortMap;
    }

    public Map<String, Integer> getServicePortMap() {
        return servicePortMap;
    }

    public void addProxyPort(String serviceName) {
        if (!toxiProxyPortMap.containsKey(serviceName)) {
            toxiProxyPortMap.put(serviceName, proxyPortCounter++);
        }
    }

    public void addServicePort(String serviceName, int port) {
        // Add only if port is valid
        if (!servicePortMap.containsKey(serviceName) && port > 0) {
            servicePortMap.put(serviceName, port);
        }
    }
}
