@custom-mock
Feature: Custom mock registered through mocks.custom

  Scenario: The custom container is started and its port injected into the app
    Given I have http://0.0.0.0:9000/ host
    And the echo dependency is ready
    And I have /custom-echo API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "reply" is "hello from a custom mock"
