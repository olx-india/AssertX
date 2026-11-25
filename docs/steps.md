# Built-in steps

AssertX ships these Cucumber steps in `com.olx.assertx.stepdefinitions`. `ITRunner` already registers that
package, so any `ITMain` extending it can use them without any Java — do **not** add it to your own `glue`, or Cucumber reports duplicate step definitions.

| Step | Purpose |
|------|---------|
| `Given I have <host> host` | Base URL of the app under test, e.g. `http://0.0.0.0:9000/` |
| `Given I have <path> API` | Request path, e.g. `/api/v1/orders/42` |
| `Given I have following query parameters` | Table with parameter names in the first row and values in the second |
| `Given I have following headers` | Table with header names in the first row and values in the second |
| `Given I have a request body in <contextKey>` | Uses a body you stored in the test context from a custom step |
| `Given I have a request body from file "<path>"` | Loads the body from a file under `src/test/resources`, e.g. `requests/create-order.json`; fails clearly when the file is missing |
| `Given I have following multipart file specifications` | Multipart upload definition |
| `When Execute <GET\|POST\|PUT\|PATCH\|DELETE\|HEAD\|OPTIONS> request using REST` | Sends the request with RestAssured |
| `Then Validate status code is: <code>` | Asserts the HTTP status |
| `Then the response header "<name>" is "<value>"` | Asserts a response header value, e.g. `Content-Type`; fails clearly when the header is missing |

## Example

```gherkin
Feature: Orders API

  Scenario: Fetch an order
    Given I have http://0.0.0.0:9000/ host
    And I have /api/v1/orders/42 API
    And I have following headers
      | Accept           |
      | application/json |
    When Execute GET request using REST
    Then Validate status code is: 200
```

!!! note "Tables are horizontal"
    Header and query-parameter tables put the **names in the first row** and the **values in the second row**:

    ```gherkin
    And I have following headers
      | Content-Type     | X-Default-Tenant |
      | application/json | default          |
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

Add only your own step package to `glue`; the built-in steps are inherited from `ITRunner`.
