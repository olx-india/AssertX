package com.olx.assertx.configuration;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SolrConfiguration {
    private boolean enabled;
    private int port;
    private String collectionsDir;
    private List<SolrCollectionConfiguration> collections = new ArrayList<>();
    private String image;
}
