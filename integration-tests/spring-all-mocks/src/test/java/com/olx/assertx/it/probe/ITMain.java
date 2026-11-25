package com.olx.assertx.it.probe;

import com.olx.assertx.ITRunner;
import io.cucumber.junit.CucumberOptions;

/** Starts every mock from api-testing.yml, boots ProbeApplication, runs all regression features. */
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"com.olx.assertx.it.probe.steps"},
        plugin = {"pretty", "json:target/cucumber-reports/cucumber.json"})
public class ITMain extends ITRunner {
}
