package com.github.paulcwarren.springdocs.config.data;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;

public class AbstractDataSourceConfig {

    protected DataSource createDataSource(String jdbcUrl, String driverClass, String userName, String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        return dataSource;
    }
}
