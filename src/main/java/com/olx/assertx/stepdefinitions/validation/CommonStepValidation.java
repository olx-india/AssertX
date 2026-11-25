package com.olx.assertx.stepdefinitions.validation;

import com.olx.assertx.service.model.RequestKey;
import com.olx.assertx.stepdefinitions.BaseStepDefinition;
import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import org.junit.Assert;

public class CommonStepValidation extends BaseStepDefinition {

    @Then("Validate status code is: {int}")
    public void validateStatusCode(int statusCode) {
        Object response = testContext().get(RequestKey.RESPONSE.toName());
        Assert.assertNotNull("Response object is null!", response);
        if (response instanceof io.restassured.response.Response) {
            Assert.assertEquals("Status code is not matched!", statusCode,
                    ((io.restassured.response.Response) response).getStatusCode());
        }
    }

    @Then("the response header {string} is {string}")
    public void responseHeaderIs(String name, String expected) {
        Response response = testContext().get(RequestKey.RESPONSE.toName(), Response.class);
        Assert.assertNotNull("Response object is null!", response);
        String actual = response.getHeader(name);
        Assert.assertNotNull("Response header \"" + name + "\" is missing!", actual);
        Assert.assertEquals("Response header \"" + name + "\" is not matched!", expected, actual);
    }

}
