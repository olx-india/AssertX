package com.olx.assertx.configuration;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserMocksConfiguration {
    private ExternalServicesConfiguration externalServices = new ExternalServicesConfiguration();
    private RedisConfiguration redis = new RedisConfiguration();
    private ToxiproxyConfiguration toxiproxy = new ToxiproxyConfiguration();
    private MysqlConfiguration mysql = new MysqlConfiguration();
    private LocalStackConfiguration localstack = new LocalStackConfiguration();
    private SolrConfiguration solr = new SolrConfiguration();
    private ZookeeperConfiguration zookeeper = new ZookeeperConfiguration();
    private KafkaConfiguration kafka = new KafkaConfiguration();
    private PostgreSqlConfiguration postgresql = new PostgreSqlConfiguration();
    private OpenSearchConfiguration opensearch = new OpenSearchConfiguration();
    private List<String> custom = new ArrayList<>();
}
