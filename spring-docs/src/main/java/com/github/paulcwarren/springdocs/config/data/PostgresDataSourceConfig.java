package com.github.paulcwarren.springdocs.config.data;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
@EnableJpaRepositories(basePackages = {"com.github.paulcwarren.springdocs.repositories", "org.springframework.versions"})
@Profile("postgres")
public class PostgresDataSourceConfig extends AbstractDataSourceConfig {

    @Value("#{environment.SPRINGDOCS_DS_URL}")
    private String url;

    @Value("#{environment.SPRINGDOCS_DS_USERNAME}")
    private String username;

    @Value("#{environment.SPRINGDOCS_DS_PASSWORD}")
    private String password;

    @Bean
    public DataSource dataSource() {
        return createDataSource(getUrl(url), "org.postgresql.Driver", getUsername(username), getPassword(password));
    }

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.POSTGRESQL);
		vendorAdapter.setGenerateDdl(true);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("com.github.paulcwarren.springdocs.domain");
		factory.setDataSource(dataSource());

		return factory;
	}
    
    private String getUrl(String url) {
        if (url == null) {
            url = "jdbc:postgresql://localhost/springdocs";
        }
        return url;
    }

    private String getUsername(String username) {
        if (username == null) {
            username = "postgres";
        }
        return username;
    }

    private String getPassword(String password) {
        if (password == null) {
            password = "postgres";
        }
        return password;
    }
}
