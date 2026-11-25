package com.olx.assertx.it.probe.mocks;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.olx.assertx.mocks.model.BaseService;
import com.olx.assertx.mocks.model.Healthcheck;
import com.olx.assertx.mocks.model.Networks;
import java.util.List;

/** docker-compose service model for {@link EchoServerMock}. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpEcho extends BaseService {

    private final String image;
    private final List<String> command;

    public HttpEcho(String containerName, List<String> ports, List<String> environment, Networks networks,
                    Healthcheck healthcheck, String image, List<String> command) {
        super(containerName, ports, environment, networks, healthcheck);
        this.image = image;
        this.command = command;
    }

    public String getImage() {
        return image;
    }

    public List<String> getCommand() {
        return command;
    }
}
