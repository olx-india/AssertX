package com.olx.assertx.stepdefinitions.execution;

import com.olx.assertx.service.model.RequestKey;
import com.olx.assertx.stepdefinitions.BaseRestStepDefinition;
import io.cucumber.java.en.When;

public class RestCommonStepExecution extends BaseRestStepDefinition {

    @When("^Execute (.*) request using REST$")
    public void processRequest(String requestType) {
        testContext().set(RequestKey.TYPE.toName(), requestType);
        executeRequest(requestMapper().map(testContext()));
        clearRequestHeadersAndQueryParamsAndOtherSpecs();
    }

    private void clearRequestHeadersAndQueryParamsAndOtherSpecs() {
        testContext().remove(RequestKey.HEADERS.toName());
        testContext().remove(RequestKey.QUERY_PARAMS.toName());
        testContext().remove(RequestKey.MULTIPART_SPECS.toName());
    }

}
