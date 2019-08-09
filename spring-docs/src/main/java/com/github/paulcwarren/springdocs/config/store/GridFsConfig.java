package com.github.paulcwarren.springdocs.config.store;

import org.springframework.content.mongo.config.EnableMongoStores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
@EnableMongoStores(basePackages="com.github.paulcwarren.springdocs.stores")
@Profile("gridfs")
public class GridFsConfig {

	@Bean
	public GridFsTemplate gridFsTemplate(MongoDbFactory mongoDbFactory, MongoConverter mongoConverter) throws Exception {
		return new GridFsTemplate(mongoDbFactory, mongoConverter);
	}
}
