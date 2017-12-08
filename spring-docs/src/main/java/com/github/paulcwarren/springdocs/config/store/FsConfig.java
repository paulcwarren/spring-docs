package com.github.paulcwarren.springdocs.config.store;

import internal.org.springframework.content.mongo.boot.autoconfigure.MongoContentAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
@EnableAutoConfiguration(exclude = {MongoContentAutoConfiguration.class})
@EnableFilesystemStores(basePackages = {"com.github.paulcwarren.springdocs.stores"})
@Profile("fs")
public class FsConfig {

	@Autowired
	private PageableHandlerMethodArgumentResolver pageableResolver;



	File filesystemRoot() {
		try {
			return Files.createTempDirectory("").toFile();
		} catch (IOException ioe) {}
		return null;
	}

	@Bean
	public FileSystemResourceLoader fsResourceLoader() throws Exception {
		return new FileSystemResourceLoader(filesystemRoot().getAbsolutePath());
	}
}
