package com.github.paulcwarren.springdocs.config.data;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableAutoConfiguration
@EnableMongoRepositories(basePackages = {"com.github.paulcwarren.springdocs.repositories.mongodb"})
@EntityScan(basePackages="com.github.paulcwarren.springdocs.domain")
@Profile("mongodb")
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("#{environment.SPRINGDOCS_MDB_URL}")
    private String mongoDbUrl;

    @Bean
    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory) {
        return new MongoTemplate(mongoDbFactory);
    }

    @Override
    public MongoDbFactory mongoDbFactory() {

        if (System.getenv("SPRINGDOCS_MDB_URL") != null) {

            return new SimpleMongoDbFactory(new MongoClientURI(mongoDbUrl));
        } else {

            return new SimpleMongoDbFactory(new MongoClientURI("mongodb://localhost/springdocs"));
        }
    }

    @Override
    protected String getDatabaseName() {
        return "springdocs";
    }

    @Override
    public MongoClient mongoClient() {
        return new MongoClient();
    }
}
