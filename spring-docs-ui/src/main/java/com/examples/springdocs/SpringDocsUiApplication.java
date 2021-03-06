package com.examples.springdocs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class SpringDocsUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringDocsUiApplication.class, args);
	}

	@Controller
	public static class UiController {

		@Value("#{environment.SPRINGDOCS_CS_URL}")
		private String contentServiceUrl = "http://localhost:9090/";

		@RequestMapping(value = "/", method = RequestMethod.GET)
		public String homepage() {
			return "index";
		}

		@RequestMapping(value = "/appinfo", method = RequestMethod.GET)
		@ResponseBody
		public String appinfo() {
			StringBuilder builder = new StringBuilder();
			builder.append("{\"name\" : \"Spring Docs\"");

			if (contentServiceUrl != null) {
				builder.append(", \"url\" : \"" + contentServiceUrl + "\"");
			}

			builder.append("}");
			return builder.toString();
		}
	}

}
