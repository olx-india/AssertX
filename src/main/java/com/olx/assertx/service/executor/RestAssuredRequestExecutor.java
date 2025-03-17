package com.olx.assertx.service.executor;

import com.olx.assertx.service.model.Request;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class RestAssuredRequestExecutor {
  private static final Logger LOGGER = LoggerFactory.getLogger(RestAssuredRequestExecutor.class);

  public Response executeRequest(Request request) {
    LOGGER.debug("Execute request={}", request);
    RequestSpecification requestSpecification = generateRequestSpec(request);
    Response response = RestAssured.given().spec(requestSpecification).when().log().ifValidationFails().request(request.getRequestType());
    response = this.checkRedirection(response, Method.valueOf(request.getRequestType()), requestSpecification);
    LOGGER.debug("Response={}", response);
    return response;
  }

  private RequestSpecification generateRequestSpec(Request request) {
    RequestSpecBuilder builder = new RequestSpecBuilder();
    if (request.getBaseURL() != null) {
      builder.setBaseUri(request.getBaseURL());
    }
    if (request.getApiPath() != null) {
      builder.setBasePath(request.getApiPath());
    }

    if (request.getRequestBodyJson() != null) {
      builder.setBody(request.getRequestBodyJson());
    }

    if (request.getHeaders() != null) {
      builder.addHeaders(request.getHeaders());
    }

    if (request.getQueryParameters() != null) {
      builder.addQueryParams(request.getQueryParameters());
    }

    if(request.getMultipartFileSpecifications() != null && request.getMultipartFileSpecifications().containsKey("filePath")){
      builder.addMultiPart(request.getMultipartFileSpecifications().getOrDefault("multipartParamName", "file"),
              new File(request.getMultipartFileSpecifications().get("filePath")),
              request.getMultipartFileSpecifications().getOrDefault("contentType", "multipart/form-data"));
    }

    return builder.build();

  }

  private Response checkRedirection(Response response, Method requestType, RequestSpecification requestSpecification) {
    if (response.getStatusCode() == 301) {
      String redirectURL = response.getHeader("Location");
      return RestAssured.given().spec(requestSpecification).request(requestType, redirectURL);
    } else {
      return response;
    }
  }
}
