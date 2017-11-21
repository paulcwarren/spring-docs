package com.github.paulcwarren.springdocs.config.store;

import internal.org.springframework.content.mongo.boot.autoconfigure.MongoContentAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.cloud.connector.nfs.NFSServiceConnector;
import org.springframework.content.fs.config.EnableFilesystemStores;
import org.springframework.content.fs.io.FileSystemResourceLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

@Configuration
@EnableAutoConfiguration(exclude = {MongoContentAutoConfiguration.class})
@EnableFilesystemStores(basePackages = {"com.github.paulcwarren.springdocs.stores"})
@Profile("nfs-cloud")
public class FsCloudConfig extends AbstractCloudConfig {

    @Bean
    public NFSServiceConnector nfs() {
        return connectionFactory().service(NFSServiceConnector.class);
    }

	@Bean
	public FileSystemResourceLoader fsResourceLoader() throws Exception {
        Assert.isTrue(nfs().getVolumeMounts().length == 1);
        return new FileSystemResourceLoader(nfs().getVolumeMounts()[0].getContainerDir());
	}

}
