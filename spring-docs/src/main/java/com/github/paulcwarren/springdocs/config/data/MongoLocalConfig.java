package com.github.paulcwarren.springdocs.config.data;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
@Profile("mongodb-local")
public class MongoLocalConfig {

    @Bean
    public MongoDbFactory mongoDbFactory() {
//        try {
            return new SimpleMongoDbFactory(new MongoClient(), "spring-docs");
//        } catch (UnknownHostException e) {
//            throw new RuntimeException("Error creating MongoDbFactory: " + e);
//        }
    }

}
