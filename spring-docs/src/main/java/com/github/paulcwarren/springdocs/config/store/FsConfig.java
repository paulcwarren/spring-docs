package com.github.paulcwarren.springdocs.config.store;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.config.FilesystemStoreConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;

@Configuration
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class, MongoDataAutoConfiguration.class})
@EnableFilesystemStores(basePackages = {"com.github.paulcwarren.springdocs.stores"})
@Profile("fs")
public class FsConfig {

	private static final Log logger = LogFactory.getLog(FsConfig.class);

	@Bean
	public FilesystemStoreConfigurer configurer() {
		return new FilesystemStoreConfigurer() {

			@Override
			public void configureFilesystemStoreConverters(ConverterRegistry registry) {
				registry.addConverter(new Converter<String, String>() {

					@Override
					public String convert(String source) {
						// Standard UID format
						return String.format("%s", source.toString());
					}
				});
			}
		};
	}
}
