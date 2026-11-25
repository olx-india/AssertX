@databases
Feature: MySQL and PostgreSQL mocks
  Each database is seeded from its dataDir init script, then read and written through the app.

  Background:
    Given I have http://0.0.0.0:9000/ host

  Scenario Outline: <db> is seeded from dataDir and accepts writes
    Given the <db> dependency is ready
    And I have /<db>/users/1 API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "name" is "Ada"
    Given I have /<db>/users API
    And I have following headers
      | Content-Type     |
      | application/json |
    And I have a request body from file "requests/new-user.json"
    When Execute POST request using REST
    Then Validate status code is: 201
    Given I have /<db>/users/3 API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "name" is "Linus"

    Examples:
      | db         |
      | mysql      |
      | postgresql |
