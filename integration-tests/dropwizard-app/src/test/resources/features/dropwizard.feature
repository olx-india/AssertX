@dropwizard
Feature: Dropwizard application booted by AssertX
  Toxiproxy is disabled, so the app talks to the containers directly on the overridden ports.

  Background:
    Given I have http://0.0.0.0:9100/ host
    And the Dropwizard app's dependencies are ready

  Scenario: Greet a user from the HTTP mock on a custom external-services port
    Given I have /api/v1/greetings/42 API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "message" is "Hello, Ada!"

  Scenario: Unknown user
    Given I have /api/v1/greetings/1 API
    When Execute GET request using REST
    Then Validate status code is: 404

  Scenario: Redis on a custom port with an overridden image
    Given I have /api/v1/visits/home API
    When Execute POST request using REST
    Then Validate status code is: 200
    And the response field "visits" is "1"
    And the AssertX Jedis client sees "visits:home" = "1"
    When Execute POST request using REST
    Then the response field "visits" is "2"
