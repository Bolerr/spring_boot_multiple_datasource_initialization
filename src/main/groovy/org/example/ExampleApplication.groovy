package org.example

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.support.SpringBootServletInitializer

@SpringBootApplication
@EnableAutoConfiguration(exclude = [DataSourceAutoConfiguration.class])
class ExampleApplication extends SpringBootServletInitializer {

	static void main(String[] args) {
		SpringApplication.run(ExampleApplication.class, args)
	}

	@Override
	protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
		return application.sources(ExampleApplication.class)
	}
}
