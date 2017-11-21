package com.github.paulcwarren.springdocs;

import com.github.paulcwarren.springdocs.config.SpringApplicationContextInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * It is usually sufficient to use the @SpringBootApplication annotation on this class.
 *
 * This example has multiple spring data/content dependencies that are designed to be user-activated
 * with profiles.
 *
 * Each spring data/content dependency imports auto-configuration classes and these are *not* aware of
 * the profiles configured for this application.  As a result these auto-configuration classes will attempt
 * to configure their beans into the application regardless of whether the user intends to use
 * them, or not.  Sometimes, this will cause unintended behavior.
 *
 * As a consequence we need finer-grained control over auto-configuration through `EnableAutoConfiguration`
 * exclusions so that we can exclude any auto-configuration that should not be included when a profile is
 * activated by the user.  For example, we dont want any mongo auto-configuration when the user intends to
 * use JPA data sources.
 */

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
