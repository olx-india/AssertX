package com.olx.assertx.configuration;

import lombok.Data;

@Data
public class SolrCollectionConfiguration {
    private String name;
    private String configPath;
    private boolean primary;
}
