package com.olx.assertx.mocks.factory;

import com.olx.assertx.mocks.*;
import com.olx.assertx.mocks.model.MockType;

public class MockFactory {

    public static BaseMock getMock(MockType mockType) {
        BaseMock mock = null;
        if (mockType.equals(MockType.EXTERNAL_SERVICES)) {
            mock = new ExternalServicesMock();
        } else if (mockType.equals(MockType.GLOBAL)) {
            mock = new GlobalMock();
        } else if (mockType.equals(MockType.REDIS)) {
            mock = new RedisMock();
        } else if (mockType.equals(MockType.TOXIPROXY)) {
            mock = new ToxiProxyMock();
        } else if (mockType.equals(MockType.USER_SPECIFIED)) {
            mock = new UserSpecifiedMock();
        } else if (mockType.equals(MockType.MYSQL)) {
            mock = new MysqlMock();
        } else if (mockType.equals(MockType.LOCALSTACK)) {
            mock = new LocalStackMock();
        } else if (mockType.equals(MockType.SOLR)) {
            mock = new SolrMock();
        } else if (mockType.equals(MockType.ZOOKEEPER)) {
            mock = new ZookeeperMock();
        } else if (mockType.equals(MockType.KAFKA)) {
            mock = new KafkaMock();
        } else if (mockType.equals(MockType.POSTGRESQL)) {
            mock = new PostgreSqlMock();
        } else if (mockType.equals(MockType.OPENSEARCH)) {
            mock = new OpenSearchMock();
        }
        return mock;
    }

    public static BaseJSMock getJSMock(MockType mockType) {
        BaseJSMock mock = null;
        if (mockType.equals(MockType.GLOBAL)) {
            mock = new GlobalMock();
        } else if (mockType.equals(MockType.USER_SPECIFIED)) {
            mock = new UserSpecifiedMock();
        }
        return mock;
    }

}
