package com.github.paulcwarren.springdocs.config.data;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableJpaRepositories(basePackages = {"com.github.paulcwarren.springdocs.repositories", "org.springframework.versions"})
@EntityScan(basePackages="com.github.paulcwarren.springdocs.domain")
@Profile("in-memory")
public class InMemoryDataSourceConfig extends AbstractDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        return createDataSource("jdbc:h2:mem:testdb", "org.h2.Driver", "sa", "");
    }

}
