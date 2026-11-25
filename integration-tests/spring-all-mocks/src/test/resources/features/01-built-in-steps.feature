@http
Feature: Built-in AssertX request and validation steps
  Every built-in step, against the probe's /echo endpoint which reflects the request back.

  Background:
    Given I have http://0.0.0.0:9000/ host

  Scenario: GET with query parameters and headers
    Given I have /echo API
    And I have following query parameters
      | q        | page |
      | assertx  | 2    |
    And I have following headers
      | X-Trace-Id | Accept           |
      | trace-123  | application/json |
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response header "X-Echo-Method" is "GET"
    And the response header "Content-Type" is "application/json"
    And the response field "method" is "GET"
    And the response field "query.q" is "assertx"
    And the response field "query.page" is "2"
    And the response field "traceId" is "trace-123"

  Scenario: POST with a request body from a file
    Given I have /echo API
    And I have following headers
      | Content-Type     |
      | application/json |
    And I have a request body from file "requests/order.json"
    When Execute POST request using REST
    Then Validate status code is: 200
    And the response field "method" is "POST"
    And the response field "contentType" is "application/json"
    And the response field "body" matches "(?s).*\"orderId\": \"ORD-1\".*"

  Scenario: PUT with a request body taken from the test context
    Given I store the text "{\"name\": \"from-context\"}" in the test context as "contextBody"
    And I have /echo API
    And I have following headers
      | Content-Type     |
      | application/json |
    And I have a request body in contextBody
    When Execute PUT request using REST
    Then Validate status code is: 200
    And the response field "method" is "PUT"
    And the response field "body" is "{\"name\": \"from-context\"}"

  Scenario Outline: <method> is sent with the right verb
    Given I have /echo API
    And I have following headers
      | Content-Type     |
      | application/json |
    And I have a request body from file "requests/order.json"
    When Execute <method> request using REST
    Then Validate status code is: 200
    And the response header "X-Echo-Method" is "<method>"

    Examples:
      | method |
      | PATCH  |
      | DELETE |

  Scenario: HEAD returns headers and no body
    Given I have /echo API
    When Execute HEAD request using REST
    Then Validate status code is: 200
    And the response header "X-Echo-Method" is "HEAD"
    And the response body is empty

  Scenario: OPTIONS returns the allowed methods
    Given I have /echo API
    When Execute OPTIONS request using REST
    Then Validate status code is: 204
    And the response header "Allow" is "GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS"

  Scenario: Multipart file upload
    Given I have /upload API
    And I have following multipart file specifications
      | filePath                            | multipartParamName | contentType |
      | src/test/resources/files/upload.txt | document           | text/plain  |
    When Execute POST request using REST
    Then Validate status code is: 200
    And the response field "filename" is "upload.txt"
    And the response field "content" is "hello from a multipart upload"

  Scenario: A 301 redirect is followed
    Given I have /moved API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "query.redirected" is "true"
