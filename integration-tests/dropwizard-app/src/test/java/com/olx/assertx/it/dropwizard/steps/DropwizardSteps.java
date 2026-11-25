package com.olx.assertx.it.dropwizard.steps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.olx.assertx.service.model.RequestKey;
import com.olx.assertx.stepdefinitions.BaseStepDefinition;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class DropwizardSteps extends BaseStepDefinition {

    @Given("the Dropwizard app's dependencies are ready")
    public void ready() throws InterruptedException {
        long deadline = System.currentTimeMillis() + 180_000;
        String last = "";
        while (System.currentTimeMillis() < deadline) {
            Response r = RestAssured.get("http://0.0.0.0:9100/api/v1/ready");
            if (r.statusCode() == 200) {
                return;
            }
            last = r.asString();
            Thread.sleep(2000);
        }
        fail("Dropwizard app dependencies not ready: " + last);
    }

    @Then("the response field {string} is {string}")
    public void responseFieldIs(String jsonPath, String expected) {
        Response response = testContext().get(RequestKey.RESPONSE.toName(), Response.class);
        assertEquals(expected, response.jsonPath().getString(jsonPath));
    }

    @Then("the AssertX Jedis client sees {string} = {string}")
    public void jedisSees(String key, String value) {
        assertEquals(value, getJedisClient().get(key));
    }
}
