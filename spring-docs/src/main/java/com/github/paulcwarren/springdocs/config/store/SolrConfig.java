//package com.github.paulcwarren.springdocs.config.store;
//
//import org.apache.solr.client.solrj.SolrClient;
//import org.apache.solr.client.solrj.impl.HttpSolrClient;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//
//@Configuration
//@Profile("solr")
//public class SolrConfig {
//
//    @Value("#{environment.SPRINGDOCS_SOLR_URL}")
//    private String solrUrl;
//
//    @Bean
//    public SolrClient solrClient() {
//        return new HttpSolrClient.Builder(getUrl(solrUrl)).build();
//    }
//
//    private String getUrl(String url) {
//        if (url == null) {
//            url = "http://localhost:8983/solr/solr";
//        }
//        return url;
//    }
//}