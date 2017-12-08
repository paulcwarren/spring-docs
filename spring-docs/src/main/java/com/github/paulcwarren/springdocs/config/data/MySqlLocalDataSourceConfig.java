package com.github.paulcwarren.springdocs.config.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = {"com.github.paulcwarren.springdocs.repositories.jpa"})
@EntityScan(basePackages="com.github.paulcwarren.springdocs.domain")
@Profile("mysql-local")
public class MySqlLocalDataSourceConfig extends AbstractLocalDataSourceConfig {

    @Bean
    public DataSource dataSource() {
        return createDataSource("jdbc:mysql://localhost/docs", "com.mysql.jdbc.Driver", "root", "");
    }

}
