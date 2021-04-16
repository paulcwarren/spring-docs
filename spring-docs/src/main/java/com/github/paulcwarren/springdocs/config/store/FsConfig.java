package com.github.paulcwarren.springdocs.config.store;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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

	@Bean	
	@ConditionalOnExpression("'${spring.content.fs.directoryFormat}'=='flat'")
	public FilesystemStoreConfigurer flatConfigurer() {
		return new FilesystemStoreConfigurer() {

			@Override
			public void configureFilesystemStoreConverters(ConverterRegistry registry) {
				registry.addConverter(new Converter<String, String>() {

					// Single Directory
					@Override
					public String convert(String source) {
						// Standard UID format
						return String.format("%s", source.toString());
					}
					
				});
			}
		};
	}
	
	@Bean
	@ConditionalOnExpression("'${spring.content.fs.directoryFormat}'=='hierarchy'")
	public FilesystemStoreConfigurer hierarchyConfigurer() {
		return new FilesystemStoreConfigurer() {

			@Override
			public void configureFilesystemStoreConverters(ConverterRegistry registry) {
				registry.addConverter(new Converter<String, String>() {
					
					// Directory hierarchy
					@Override
					public String convert(String source) {
						// Standard UID format
						return String.format("/%s", source.toString().replaceAll("-", "")).replaceAll("..", "$0/");
					}									
				});
			}
		};
	}
	
}
