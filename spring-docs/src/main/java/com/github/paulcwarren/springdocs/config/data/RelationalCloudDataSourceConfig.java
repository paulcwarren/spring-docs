package com.github.paulcwarren.springdocs.config.data;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@Configuration
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableJpaRepositories(basePackages = {"com.github.paulcwarren.springdocs.repositories.jpa"})
@EntityScan(basePackages="com.github.paulcwarren.springdocs.domain")
@Profile({"mysql-cloud", "postgres-cloud", "oracle-cloud", "sqlserver-cloud"})
public class RelationalCloudDataSourceConfig extends AbstractCloudConfig {

    private static final Log logger = LogFactory.getLog(RelationalCloudDataSourceConfig.class);

    @Bean
    public DataSource dataSource() {
        logger.info(String.format("Creating cloud relational datasource: %s", connectionFactory().dataSource()));
        return connectionFactory().dataSource();
    }

}
