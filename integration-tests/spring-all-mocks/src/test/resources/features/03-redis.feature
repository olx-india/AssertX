@redis
Feature: Redis mock

  Background:
    Given I have http://0.0.0.0:9000/ host
    And the redis dependency is ready

  Scenario: Write, read and delete a key through the app
    Given I have /redis/keys/greeting API
    And I have following headers
      | Content-Type     |
      | application/json |
    And I store the text "{\"value\": \"hello\"}" in the test context as "redisValue"
    And I have a request body in redisValue
    When Execute PUT request using REST
    Then Validate status code is: 201
    And Redis key "greeting" holds "hello" according to the AssertX Jedis client
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "value" is "hello"
    When Execute DELETE request using REST
    Then Validate status code is: 204
    When Execute GET request using REST
    Then Validate status code is: 404

  Scenario: Data written by the AssertX Jedis client is visible to the app
    Given Redis key "seeded" is set to "by-jedis" through the AssertX Jedis client
    And I have /redis/keys/seeded API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "value" is "by-jedis"

  Scenario: The redis.conf from configFilePath is applied
    Given I have /redis/config/maxmemory-policy API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "value" is "allkeys-lru"
