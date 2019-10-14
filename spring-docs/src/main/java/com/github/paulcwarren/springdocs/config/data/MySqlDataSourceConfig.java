package com.github.paulcwarren.springdocs.config.data;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(basePackages = {"com.github.paulcwarren.springdocs.repositories", "org.springframework.versions"})
@EntityScan(basePackages="com.github.paulcwarren.springdocs.domain")
@Profile("mysql")
public class MySqlDataSourceConfig extends AbstractDataSourceConfig {

    @Value("#{environment.SPRINGDOCS_DS_URL}")
    private String url;

    @Value("#{environment.SPRINGDOCS_DS_USERNAME}")
    private String username;

    @Value("#{environment.SPRINGDOCS_DS_PASSWORD}")
    private String password;

    @Bean
    public DataSource dataSource() {
        return createDataSource(getUrl(url), "com.mysql.jdbc.Driver", getUsername(username), getPassword(password));
    }

    private String getUrl(String url) {
        if (url == null) {
            url = "jdbc:mysql://localhost/springdocs?emulateLocators=true&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        }
        return url;
    }

    private String getUsername(String username) {
        if (username == null) {
            username = "root";
        }
        return username;
    }

    private String getPassword(String password) {
        if (password == null) {
            password = "";
        }
        return password;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.MYSQL);
        vendorAdapter.setGenerateDdl(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.github.paulcwarren.springdocs.domain");
        factory.setDataSource(dataSource());

        Map<String,Object> props = factory.getJpaPropertyMap();
        props.put("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        factory.setJpaPropertyMap(props);

        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {

        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }
}
