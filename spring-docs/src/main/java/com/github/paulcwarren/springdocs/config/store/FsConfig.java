package com.github.paulcwarren.springdocs.config.store;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Configuration
@Profile("fs-local")
public class FsConfig {

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
