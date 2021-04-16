package com.github.paulcwarren.springdocs.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Base security configuration provides Authentication object required by
 * LockingAndVersioningRepository. Currently all client calls use Spring's
 * 'anonymousUser'.
 *
 * @author Lauren Ward
 *
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

	protected static String REALM = "SPRING_CONTENT";

	@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		// Enable if spring-doc apps supports user accounts in the future
		// auth.inMemoryAuthentication().withUser(User.withDefaultPasswordEncoder().username("user").password("secret").roles("USER").build());

	}

	@Bean
	public AuthenticationEntryPoint getBasicAuthEntryPoint() {
		return new AuthenticationEntryPoint();
	}

	
	/**
	 * Enable calls from client app
	 * @return
	 */
	@Bean
	public CorsFilter corsFilter() {
	    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
	    final CorsConfiguration config = new CorsConfiguration();
	    config.setAllowCredentials(true);
	    // Don't do this in production, use a proper list  of allowed origins
	    config.setAllowedOrigins(Collections.singletonList("http://localhost:8080"));
	    config.setAllowedHeaders(Arrays.asList("Origin", "Content-Type", "Accept"));
	    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH"));
	    source.registerCorsConfiguration("/**", config);
	    return new CorsFilter(source);
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		   http.csrf().disable()
	        .authorizeRequests()
	        .antMatchers("/admin/**").hasRole("ADMIN")
	        .and().httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint())
	        .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
	}

}