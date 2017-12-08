package com.github.paulcwarren.springdocs.config.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
@Profile("gridfs")
public class GridFsConfig {

	@Autowired private MongoDbFactory mongoDbFactory;
	@Autowired private MongoConverter mongoConverter;

	@Bean
	public GridFsTemplate gridFsTemplate() throws Exception {
		return new GridFsTemplate(mongoDbFactory, mongoConverter);
	}
}
