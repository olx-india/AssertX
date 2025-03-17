package com.olx.assertx.application;

import com.olx.assertx.application.model.ApplicationFramework;
import com.olx.assertx.configuration.UserTestConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class SpringTestApplication implements TestApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringTestApplication.class);
    private static ApplicationContext springApplicationContext;
    private final UserTestConfiguration userTestConfiguration;

    public static final String ARG_PROFILE = "spring.profiles.active";

    public SpringTestApplication(final UserTestConfiguration userTestConfiguration) {
        this.userTestConfiguration = userTestConfiguration;
    }

    @Override
    public boolean isEnabled() {
        return this.userTestConfiguration.getApplication().getSpring() != null;
    }

    @Override
    public void addArgs(List<String> args) {
        LOGGER.info("Add arguments for application of type={}", getApplicationFramework());
        args.add(
                String.format(
                        ARG_FORMAT, ARG_PROFILE,
                        this.userTestConfiguration.getApplication().getSpring().getProfile()
                )
        );
    }

    @Override
    public void start(String[] args) throws ClassNotFoundException {
        LOGGER.info("Start application of type={} with args={}",  getApplicationFramework(), args);
        springApplicationContext = SpringApplication.run(Class.forName(this.userTestConfiguration.getMainClass()), args);
    }

    @Override
    public void stop() {
        LOGGER.info("Exiting application of type={}", getApplicationFramework());
        SpringApplication.exit(springApplicationContext);
    }

    @Override
    public ApplicationFramework getApplicationFramework() {
        return ApplicationFramework.SPRING;
    }
}
