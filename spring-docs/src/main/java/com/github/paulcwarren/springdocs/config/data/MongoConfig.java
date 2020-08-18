package com.github.paulcwarren.springdocs.config.data;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableAutoConfiguration
@EnableMongoRepositories(basePackages = {"com.github.paulcwarren.springdocs.repositories"})
@EntityScan(basePackages="com.github.paulcwarren.springdocs.domain")
@Profile("mongodb")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("#{environment.SPRINGDOCS_MDB_URL}")
    private String mongoDbUrl = "mongodb://localhost:27017";

    @Override
    protected String getDatabaseName() {
        return "springdocs";
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoDbUrl);
    }

    @Bean
    public MongoDatabaseFactory mongoDbFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoClient(), getDatabaseName());
    }
}
