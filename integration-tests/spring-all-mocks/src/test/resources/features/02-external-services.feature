@external-services
Feature: HTTP mocks served by the external-services container
  JS routes from userSpecified, an npm dependency, multiple endpoints per route, and a SOAP/XML route.

  Background:
    Given I have http://0.0.0.0:9000/ host
    And the user dependency is ready
    And the payment dependency is ready

  Scenario: GET route returns mock data and uses the uuid npm dependency
    Given I have /users/42 API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "name" is "Ada"
    And the response field "requestId" matches "[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"

  Scenario: Mock status codes reach the app
    Given I have /users/404 API
    When Execute GET request using REST
    Then Validate status code is: 404

  Scenario: POST route on the same mock
    Given I have /users API
    And I have following headers
      | Content-Type     |
      | application/json |
    And I have a request body from file "requests/new-user.json"
    When Execute POST request using REST
    Then Validate status code is: 201
    And the response field "name" is "Linus"
    Given I have /users/3 API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "name" is "Linus"

  Scenario: SOAP route answers with XML
    Given I have /payments API
    And I have following headers
      | Content-Type     |
      | application/json |
    And I have a request body from file "requests/order.json"
    When Execute POST request using REST
    Then Validate status code is: 200
    And the response field "status" is "APPROVED"
