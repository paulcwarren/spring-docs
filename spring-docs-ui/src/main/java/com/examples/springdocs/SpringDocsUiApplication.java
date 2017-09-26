package com.examples.springdocs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringBootApplication
public class SpringDocsUiApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(SpringDocsUiApplication.class, args);
	}
	
	@Controller
	public static class UiController {

	    @RequestMapping(value="/",method = RequestMethod.GET)
	    public String homepage(){
	        return "index";
	    }
	}
}
