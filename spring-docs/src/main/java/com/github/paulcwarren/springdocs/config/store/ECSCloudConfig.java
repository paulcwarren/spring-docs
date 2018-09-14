package com.github.paulcwarren.springdocs.config.store;

import com.emc.ecs.connector.spring.S3Connector;
import internal.org.springframework.content.mongo.boot.autoconfigure.MongoContentAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.content.s3.config.EnableS3ContentRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableAutoConfiguration(exclude = {MongoContentAutoConfiguration.class})
@EnableS3ContentRepositories(basePackages="com.github.paulcwarren.springdocs.stores")
@Profile("ecs-cloud")
public class ECSCloudConfig extends AbstractCloudConfig {

    @Bean
    public S3Connector s3() {
        return connectionFactory().service(S3Connector.class);
    }

    @Bean
    public String bucket() {
        return s3().getBucket();
    }
}
