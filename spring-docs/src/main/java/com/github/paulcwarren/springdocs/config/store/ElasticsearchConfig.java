package com.github.paulcwarren.springdocs.config.store;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import pl.allegro.tech.embeddedelasticsearch.EmbeddedElastic;

import org.springframework.content.elasticsearch.EnableElasticsearchFulltextIndexing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient client() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }
}
