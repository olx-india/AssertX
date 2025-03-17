package com.olx.assertx.application;

import com.olx.assertx.application.model.ApplicationFramework;
import com.olx.assertx.configuration.UserTestConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DropwizardTestApplication implements TestApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(DropwizardTestApplication.class);
    private final UserTestConfiguration userTestConfiguration;

    public static final String ARG_SERVER = "server";

    @ClassRule
    public static DropwizardAppRule<io.dropwizard.Configuration> dropwizardAppRule;

    public DropwizardTestApplication(final UserTestConfiguration userTestConfiguration) {
        this.userTestConfiguration = userTestConfiguration;
    }

    @Override
    public boolean isEnabled() {
        return this.userTestConfiguration.getApplication().getDropwizard() != null;
    }

    @Override
    public void addArgs(List<String> args) {
        LOGGER.info("Add arguments for application of type={}", getApplicationFramework());
        args.add(ARG_SERVER);
        args.add(this.userTestConfiguration.getApplication().getDropwizard().getConfigPath());
    }

    @Override
    public void start(String[] args) throws Exception {
        LOGGER.info("Start application of type={} with args={}",  getApplicationFramework(), args);
        dropwizardAppRule = new DropwizardAppRule(Class.forName(this.userTestConfiguration.getMainClass()),
                userTestConfiguration.getApplication().getDropwizard().getConfigPath());
        dropwizardAppRule.newApplication().run(args);
    }

    @Override
    public void stop() {
    }

    @Override
    public ApplicationFramework getApplicationFramework() {
        return ApplicationFramework.DROPWIZARD;
    }
}
