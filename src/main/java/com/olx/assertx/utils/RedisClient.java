package com.olx.assertx.utils;

import com.olx.assertx.mocks.model.MockType;
import com.olx.assertx.service.model.PropertyKey;
import redis.clients.jedis.Jedis;

public class RedisClient {

    private RedisClient() {
    }

    private static class BillPughSingleton {
        private BillPughSingleton() {
            JEDIS_INSTANCE.connect();
        }

        private static final Jedis JEDIS_INSTANCE = new Jedis(System.getProperty(PropertyKey.DOCKER_HOST.getValue()),
                PortManager.getInstance().getServicePort(MockType.REDIS.getServiceName()));
    }

    public static Jedis getJedisInstance() {
        return BillPughSingleton.JEDIS_INSTANCE;
    }
}
