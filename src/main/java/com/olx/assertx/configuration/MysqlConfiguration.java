package com.olx.assertx.configuration;

import lombok.Data;

@Data
public class MysqlConfiguration {
  private boolean enabled;
  private int port;
  private String db;
  private String dataDir;
  private String image;
}
