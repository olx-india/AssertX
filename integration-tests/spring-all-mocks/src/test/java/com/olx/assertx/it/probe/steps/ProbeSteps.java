package com.olx.assertx.it.probe.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.olx.assertx.service.model.RequestKey;
import com.olx.assertx.stepdefinitions.BaseStepDefinition;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;

/** Custom steps layered on the AssertX test context. */
public class ProbeSteps extends BaseStepDefinition {

    private static final String APP = "http://0.0.0.0:9000";
    private static final long READY_TIMEOUT_MS = Long.getLong("probe.readyTimeoutMs", 240_000);

    private Response response() {
        Response response = testContext().get(RequestKey.RESPONSE.toName(), Response.class);
        assertNotNull("No response in the test context — did a request step run?", response);
        return response;
    }

    /**
     * AssertX starts containers without waiting for their health checks, so poll the probe's
     * /ready endpoint (a real round trip through Toxiproxy) until the dependency answers.
     */
    @Given("the {word} dependency is ready")
    public void dependencyIsReady(String dependency) throws InterruptedException {
        long deadline = System.currentTimeMillis() + READY_TIMEOUT_MS;
        String last = "";
        while (System.currentTimeMillis() < deadline) {
            Response r = RestAssured.get(APP + "/ready/" + dependency);
            if (r.statusCode() == 200) {
                return;
            }
            last = r.asString();
            Thread.sleep(2000);
        }
        fail(dependency + " was not ready after " + READY_TIMEOUT_MS + " ms; last answer: " + last);
    }

    @Given("I store the text {string} in the test context as {string}")
    public void storeText(String text, String key) {
        testContext().set(key, text);
    }

    @Then("the response field {string} is {string}")
    public void responseFieldIs(String jsonPath, String expected) {
        assertEquals("JSON field " + jsonPath, expected, response().jsonPath().getString(jsonPath));
    }

    @Then("the response field {string} matches {string}")
    public void responseFieldMatches(String jsonPath, String regex) {
        String actual = response().jsonPath().getString(jsonPath);
        assertNotNull("JSON field " + jsonPath + " is missing", actual);
        assertTrue("JSON field " + jsonPath + "=" + actual + " does not match " + regex, actual.matches(regex));
    }

    @Then("the response field {string} contains {string}")
    public void responseFieldContains(String jsonPath, String expected) {
        String actual = String.valueOf(response().jsonPath().getList(jsonPath));
        assertTrue("JSON field " + jsonPath + "=" + actual + " does not contain " + expected, actual.contains(expected));
    }

    @Then("the response body is empty")
    public void responseBodyIsEmpty() {
        assertEquals("", response().asString());
    }
}
