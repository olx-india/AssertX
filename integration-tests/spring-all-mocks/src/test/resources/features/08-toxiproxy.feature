@toxiproxy
Feature: Toxiproxy chaos on mocks
  With toxiproxy enabled the app reaches every mock through a proxy named after it.

  Background:
    Given I have http://0.0.0.0:9000/ host
    And the user dependency is ready

  Scenario: Latency on a downstream HTTP mock makes the app time out
    Given the "user" proxy adds 3000 ms of latency
    And I have /users/42 API
    When Execute GET request using REST
    Then Validate status code is: 504
    When the "user" proxy is restored
    And Execute GET request using REST
    Then Validate status code is: 200

  Scenario: A disabled downstream HTTP mock is unreachable
    Given the "user" proxy is disabled
    And I have /users/42 API
    When Execute GET request using REST
    Then Validate status code is: 503

  Scenario: A disabled database proxy cuts the app off from Redis
    Given the redis dependency is ready
    And the "redis" proxy is disabled
    And I have /ready/redis API
    When Execute GET request using REST
    Then Validate status code is: 503
    When the "redis" proxy is restored
    And Execute GET request using REST
    Then Validate status code is: 200
