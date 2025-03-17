package com.olx.assertx.stepdefinitions.validation;

import com.olx.assertx.service.model.RequestKey;
import com.olx.assertx.stepdefinitions.BaseStepDefinition;
import io.cucumber.java.en.Then;
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

}
