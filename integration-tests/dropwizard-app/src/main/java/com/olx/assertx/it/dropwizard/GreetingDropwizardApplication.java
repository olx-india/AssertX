package com.olx.assertx.it.dropwizard;

import io.dropwizard.Application;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.commons.text.StringSubstitutor;
import org.apache.commons.text.lookup.StringLookupFactory;

/**
 * Dropwizard service booted by AssertX. AssertX passes mock ports as system properties named after each
 * mock, so the config file is run through a system-property substitutor ({@code ${user}}, {@code ${redis}}).
 */
public class GreetingDropwizardApplication extends Application<GreetingConfiguration> {

    public static void main(String[] args) throws Exception {
        new GreetingDropwizardApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<GreetingConfiguration> bootstrap) {
        bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
                bootstrap.getConfigurationSourceProvider(),
                new StringSubstitutor(StringLookupFactory.INSTANCE.systemPropertyStringLookup())));
    }

    @Override
    public void run(GreetingConfiguration config, Environment environment) {
        environment.jersey().register(new GreetingResource(config, environment.getObjectMapper()));
    }
}
