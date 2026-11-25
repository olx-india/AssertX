package com.olx.assertx.it.probe.mocks;

import com.olx.assertx.mocks.BaseCustomMock;
import com.olx.assertx.mocks.model.BaseService;
import com.olx.assertx.mocks.model.Networks;
import com.olx.assertx.mocks.model.Service1Net;
import java.util.List;

/**
 * A custom mock registered through {@code mocks.custom} in api-testing.yml: a tiny HTTP server that
 * always answers with a fixed text. Its port reaches the app as the {@code ${echo}} system property.
 */
public class EchoServerMock implements BaseCustomMock {

    static final int PORT = 35678;

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public BaseService map(int port) {
        return new HttpEcho(getServiceName(),
                List.of(port + ":" + port),
                List.of(),
                Networks.builder().service1NetName(Service1Net.builder().build()).build(),
                null,
                "hashicorp/http-echo:1.0",
                List.of("-listen=:" + port, "-text=hello from a custom mock"));
    }

    @Override
    public String getServiceName() {
        return "echo";
    }

    @Override
    public int getServicePort() {
        return PORT;
    }
}
