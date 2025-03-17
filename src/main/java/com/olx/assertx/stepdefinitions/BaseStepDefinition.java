package com.olx.assertx.stepdefinitions;

import com.olx.assertx.client.ToxiProxyClient;
import com.olx.assertx.context.CucumberTestContext;
import com.olx.assertx.utils.RedisClient;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import redis.clients.jedis.Jedis;

public class BaseStepDefinition {

    public CucumberTestContext testContext() {
        return CucumberTestContext.CONTEXT;
    }

    public ToxiproxyClient getToxiProxyClient() {
        return ToxiProxyClient.getInstance();
    }

    public Jedis getJedisClient() {
        return RedisClient.getJedisInstance();
    }

}
