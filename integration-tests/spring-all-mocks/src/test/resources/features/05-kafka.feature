@kafka
Feature: Kafka and ZooKeeper mocks

  Background:
    Given I have http://0.0.0.0:9000/ host
    And the kafka dependency is ready

  Scenario: Topics from api-testing.yml are created
    Given I have /kafka/topics API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "$" contains "orderCreated"
    And the response field "$" contains "orderCancelled"

  Scenario: A message published by the app can be consumed back
    Given I have /kafka/topics/orderCreated API
    And I have following headers
      | Content-Type     |
      | application/json |
    And I have a request body from file "requests/order.json"
    When Execute POST request using REST
    Then Validate status code is: 201
    Given I have /kafka/topics/orderCreated/last API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "orderId" is "ORD-1"
