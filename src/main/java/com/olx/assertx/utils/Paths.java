package com.olx.assertx.utils;

import com.olx.assertx.mocks.builder.DockerComposeBuilder;
import com.olx.assertx.mocks.model.MockType;

public class Paths {
    public static final String BASE_IT_FOLDER = "it";
    public static final String MOCKS_FOLDER = BASE_IT_FOLDER  + "/mocks";
    public static final String LOGS_FOLDER = BASE_IT_FOLDER + "/logs";
    public static final String DOCKER_COMPOSE_FILE = MOCKS_FOLDER + "/" + DockerComposeBuilder.DOCKER_COMPOSE_FILENAME;
    public static final String ES_FOLDER = MOCKS_FOLDER + "/" + MockType.EXTERNAL_SERVICES.getServiceName();
    public static final String TOXI_PROXY_FOLDER = MOCKS_FOLDER + "/" + MockType.TOXIPROXY.getServiceName();

    public static final String TEMPLATE_PATH_FORMAT = "mocks/%s/template";
    public static final String TEMPLATE_ES_PATH = String.format(TEMPLATE_PATH_FORMAT, MockType.EXTERNAL_SERVICES.getServiceName());

    public static final String TARGET = "target";
    public static final String CUCUMBER_REPORTS = TARGET + "/cucumber-reports";

    public static final String FRAMEWORK_CONFIG = "/config.yml";
    public static final String USER_TEST_CONFIG = "/api-testing.yml";
}
