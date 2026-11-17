package com.example.greeting.it.steps;

import static org.junit.Assert.assertEquals;

import com.olx.assertx.service.model.RequestKey;
import com.olx.assertx.stepdefinitions.BaseStepDefinition;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;

/** Example of a custom step built on top of the AssertX test context. */
public class ResponseSteps extends BaseStepDefinition {

    @Then("the response field {string} is {string}")
    public void responseFieldIs(String jsonPath, String expected) {
        Response response = testContext().get(RequestKey.RESPONSE.toName(), Response.class);
        assertEquals(expected, response.jsonPath().getString(jsonPath));
    }
}
