package com.allergenie.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AllergenieApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AllergenieApiApplication.class, args);



		System.out.println("Application Starting");
		System.out.println("Serving on Port: 8080");
	}

}
