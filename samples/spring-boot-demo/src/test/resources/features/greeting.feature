Feature: Greeting API
  The greeting service looks the user up in user-service and says hello.

  Background:
    Given I have http://0.0.0.0:9000/ host
    And I have following headers
      | Accept | application/json |

  Scenario: Greet a known user
    Given I have /api/v1/greetings/42 API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "message" is "Hello, Ada!"

  Scenario Outline: Greet users by id
    Given I have /api/v1/greetings/<userId> API
    When Execute GET request using REST
    Then Validate status code is: <status>

    Examples:
      | userId | status |
      | 7      | 200    |
      | 404    | 404    |
