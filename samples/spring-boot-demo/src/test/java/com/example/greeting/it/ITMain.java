package com.example.greeting.it;

import com.olx.assertx.ITRunner;
import io.cucumber.junit.CucumberOptions;

/**
 * Entry point for AssertX: starts the mocks from api-testing.yml in Docker,
 * boots GreetingApplication, then runs every feature file.
 */
@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"com.example.greeting.it.steps"},   // built-in AssertX steps are inherited from ITRunner
        plugin = {"pretty", "json:target/cucumber-reports/cucumber.json"})
public class ITMain extends ITRunner {
}
