package com.github.paulcwarren.springdocs.config.store;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.content.jpa.config.EnableJpaStores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

@Configuration
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableJpaStores(basePackages = {"com.github.paulcwarren.springdocs.stores"})
@Conditional(value = { MySqlBlobStorageConfig.BlobAndMysqlProfiles.class })
public class MySqlBlobStorageConfig {

	private static final Log logger = LogFactory.getLog(MySqlBlobStorageConfig.class);

	@Autowired
	private DataSource ds;

	@Value("/org/springframework/content/jpa/schema-drop-mysql.sql")
	private ClassPathResource dropBlobSchema;

	@Value("/org/springframework/content/jpa/schema-mysql.sql")
	private ClassPathResource blobSchema;

	@Bean
	DataSourceInitializer datasourceInitializer() {
		ResourceDatabasePopulator databasePopulator =
				new ResourceDatabasePopulator();

		databasePopulator.addScript(dropBlobSchema);
		databasePopulator.addScript(blobSchema);
		databasePopulator.setIgnoreFailedDrops(true);

		DataSourceInitializer initializer = new DataSourceInitializer();
		initializer.setDataSource(ds);
		initializer.setDatabasePopulator(databasePopulator);

		return initializer;
	}

	public static class BlobAndMysqlProfiles implements Condition {

		public BlobAndMysqlProfiles() {}

		@Override
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			return context.getEnvironment().acceptsProfiles("blob")
					&& context.getEnvironment().acceptsProfiles("mysql");
		}
	}
}
