package com.github.paulcwarren.springdocs.config.store;

import org.springframework.content.solr.SolrProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.github.paulcwarren.solr.EnableSolrEmbedded;

@Configuration
@Profile("solr")
@EnableSolrEmbedded
public class SolrConfig {

    public SolrProperties solrProperties() {
        SolrProperties solrConfig =  new SolrProperties();
        solrConfig.setUrl("http://localhost:983/solr/solr");
        return solrConfig;
    }

}