# Built-in steps

AssertX ships these Cucumber steps in `com.olx.assertx.stepdefinitions`. Add that package to your `glue`
and you can write most API tests without any Java.

| Step | Purpose |
|------|---------|
| `Given I have <host> host` | Base URL of the app under test, e.g. `http://0.0.0.0:9000/` |
| `Given I have <path> API` | Request path, e.g. `/api/v1/orders/42` |
| `Given I have following query parameters` | Data table of `key \| value` rows |
| `Given I have following headers` | Data table of `header \| value` rows |
| `Given I have a request body in <contextKey>` | Uses a body you stored in the test context from a custom step |
| `Given I have following multipart file specifications` | Multipart upload definition |
| `When Execute <GET\|POST\|PUT\|PATCH\|DELETE> request using REST` | Sends the request with RestAssured |
| `Then Validate status code is: <code>` | Asserts the HTTP status |

## Example

```gherkin
Feature: Orders API

  Scenario: Fetch an order
    Given I have http://0.0.0.0:9000/ host
    And I have /api/v1/orders/42 API
    And I have following headers
      | Accept | application/json |
    When Execute GET request using REST
    Then Validate status code is: 200
```

## Writing your own steps

Extend `BaseStepDefinition` (or `BaseRestStepDefinition`) to get:

- `testContext()` — the per-thread `CucumberTestContext`; the last response is stored under `RequestKey.RESPONSE.toName()`
- `getJedisClient()` — a Jedis client connected to the Redis mock
- `getToxiProxyClient()` — inject latency, timeouts or connection resets into any mock

```java
public class ResponseSteps extends BaseStepDefinition {

    @Then("the response field {string} is {string}")
    public void responseFieldIs(String jsonPath, String expected) {
        Response response = testContext().get(RequestKey.RESPONSE.toName(), Response.class);
        assertEquals(expected, response.jsonPath().getString(jsonPath));
    }
}
```

Add your step package to `glue` next to `com.olx.assertx.stepdefinitions`.
