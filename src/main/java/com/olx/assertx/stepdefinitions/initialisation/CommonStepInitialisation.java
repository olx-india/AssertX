package com.olx.assertx.stepdefinitions.initialisation;

import com.olx.assertx.service.model.RequestKey;
import com.olx.assertx.stepdefinitions.BaseStepDefinition;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.junit.Assert;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class CommonStepInitialisation extends BaseStepDefinition {

    @Given("^I have (.*) host$")
    public void setBaseURL(String host) {
        testContext().set(RequestKey.HOST.toName(), host);
    }

    @Given("^I have (.*) API$")
    public void setAPIPath(String apiPath) {
        testContext().set(RequestKey.API_PATH.toName(), apiPath);
    }

    @Given("^I have following query parameters$")
    public void setQueryParameters(DataTable dataTable) {
        List<Map<String,String>> values = dataTable.asMaps(String.class, String.class);
        Assert.assertNotEquals("Empty query parameters table!", 0, values.size());
        testContext().set(RequestKey.QUERY_PARAMS.toName(), values.get(0));
    }

    @Given("^I have following headers$")
    public void setHeaders(DataTable dataTable) {
        List<Map<String,String>> values = dataTable.asMaps(String.class, String.class);
        Assert.assertNotEquals("Empty headers table!", 0, values.size());
        testContext().set(RequestKey.HEADERS.toName(), values.get(0));
    }

    @Given("^I have a request body in (.*)$")
    public void setRequestBody(String requestBody) {
        testContext().set(RequestKey.PAYLOAD.toName(), testContext().get(requestBody, String.class));
    }

    @Given("^I have a request body from file \"?([^\"]*)\"?$")
    public void setRequestBodyFromFile(String filePath) {
        java.nio.file.Path path = Paths.get("src/test/resources").resolve(filePath);
        Assert.assertTrue("Request body file not found: " + path, Files.exists(path));
        try {
            testContext().set(RequestKey.PAYLOAD.toName(), Files.readString(path));
        } catch (IOException e) {
            Assert.fail("Could not read request body file: " + path + " (" + e.getMessage() + ")");
        }
    }

    @Given("^I have following multipart file specifications$")
    public void setMultipartFileSpecs(DataTable dataTable){
        List<Map<String,String>> values = dataTable.asMaps(String.class, String.class);
        Assert.assertNotEquals("Empty specifications table!", 0, values.size());
        testContext().set(RequestKey.MULTIPART_SPECS.toName(), values.get(0));
    }

}
