package com.github.paulcwarren.springdocs;

import com.github.paulcwarren.springdocs.config.SpringApplicationContextInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class SpringDocsApplication extends SpringBootServletInitializer {

	public static void main(String[] args){
        new SpringApplicationBuilder(SpringDocsApplication.class).
        initializers(new SpringApplicationContextInitializer())
        .application()
        .run(args);
	}
}
