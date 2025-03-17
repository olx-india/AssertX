package com.olx.assertx.stepdefinitions.initialisation;

import com.olx.assertx.service.model.RequestKey;
import com.olx.assertx.stepdefinitions.BaseStepDefinition;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import org.junit.Assert;

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

    @Given("^I have following multipart file specifications$")
    public void setMultipartFileSpecs(DataTable dataTable){
        List<Map<String,String>> values = dataTable.asMaps(String.class, String.class);
        Assert.assertNotEquals("Empty specifications table!", 0, values.size());
        testContext().set(RequestKey.MULTIPART_SPECS.toName(), values.get(0));
    }

}
