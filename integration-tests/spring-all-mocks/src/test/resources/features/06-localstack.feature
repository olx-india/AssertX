@localstack
Feature: LocalStack mock (S3)

  Background:
    Given I have http://0.0.0.0:9000/ host
    And the localstack dependency is ready

  Scenario: The init script from dataDir has run
    Given I have /s3/seeded-bucket API
    When Execute GET request using REST
    Then Validate status code is: 200

  Scenario: Objects can be written and read back
    Given I have /s3/reports/daily.txt API
    And I have following headers
      | Content-Type |
      | text/plain   |
    And I store the text "42 orders today" in the test context as "report"
    And I have a request body in report
    When Execute PUT request using REST
    Then Validate status code is: 201
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "content" is "42 orders today"
