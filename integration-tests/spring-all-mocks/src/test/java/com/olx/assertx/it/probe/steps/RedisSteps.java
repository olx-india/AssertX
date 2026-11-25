package com.olx.assertx.it.probe.steps;

import static org.junit.Assert.assertEquals;

import com.olx.assertx.stepdefinitions.BaseStepDefinition;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

/** Exercises BaseStepDefinition#getJedisClient(), which talks to the Redis container directly. */
public class RedisSteps extends BaseStepDefinition {

    @Given("Redis key {string} is set to {string} through the AssertX Jedis client")
    public void setKey(String key, String value) {
        getJedisClient().set(key, value);
    }

    @Then("Redis key {string} holds {string} according to the AssertX Jedis client")
    public void keyHolds(String key, String value) {
        assertEquals(value, getJedisClient().get(key));
    }
}
