package com.github.paulcwarren.springdocs.config.store;

import org.springframework.content.solr.SolrProperties;
import org.springframework.context.annotation.Configuration;

import com.github.paulcwarren.solr.EnableSolrEmbedded;
import org.springframework.context.annotation.Profile;

@Configuration
//@Profile("solr")
@EnableSolrEmbedded
public class SolrConfig {

    public SolrProperties solrProperties() {
        SolrProperties solrConfig =  new SolrProperties();
        solrConfig.setUrl("http://localhost:8080/solr/solr");
        return solrConfig;
    }

}	
