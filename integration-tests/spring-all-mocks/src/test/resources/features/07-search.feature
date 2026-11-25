@search
Feature: Solr and OpenSearch mocks

  Background:
    Given I have http://0.0.0.0:9000/ host

  Scenario: Solr collection from collectionsDir indexes and finds a document
    Given the solr dependency is ready
    And I have /solr/items API
    And I have following headers
      | Content-Type     |
      | application/json |
    And I store the text "{\"id\": \"item-1\", \"title\": \"Red bicycle\"}" in the test context as "solrDoc"
    And I have a request body in solrDoc
    When Execute POST request using REST
    Then Validate status code is: 201
    Given I have /solr/items/item-1 API
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "title" is "Red bicycle"

  Scenario: OpenSearch indexes and returns a document
    Given the opensearch dependency is ready
    And I have /opensearch/listings/1 API
    And I have following headers
      | Content-Type     |
      | application/json |
    And I store the text "{\"title\": \"Blue sofa\", \"price\": 120}" in the test context as "osDoc"
    And I have a request body in osDoc
    When Execute PUT request using REST
    Then Validate status code is: 201
    When Execute GET request using REST
    Then Validate status code is: 200
    And the response field "title" is "Blue sofa"
