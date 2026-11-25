package com.olx.assertx.it.dropwizard;

import com.olx.assertx.ITRunner;
import io.cucumber.junit.CucumberOptions;

@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"com.olx.assertx.it.dropwizard.steps"},
        plugin = {"pretty", "json:target/cucumber-reports/cucumber.json"})
public class ITMain extends ITRunner {
}
