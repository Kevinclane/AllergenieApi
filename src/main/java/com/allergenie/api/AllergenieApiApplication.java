package com.allergenie.api;

import org.apache.log4j.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.javapoet.ClassName;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@SpringBootApplication
@EnableJpaRepositories
public class AllergenieApiApplication implements CommandLineRunner {
    private final Environment environment;
    private static final Logger logger = Logger.getLogger(ClassName.class.getName());

    public AllergenieApiApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(AllergenieApiApplication.class, args);
    }

    @Override
    public void run(String... args) {
        String[] activeProfiles = environment.getActiveProfiles();

        if (activeProfiles.length == 0) {
            logger.info("No active profiles");
        }

        if (Arrays.stream(activeProfiles).noneMatch(env -> (env.equalsIgnoreCase("local")))) {
            logger.info("Running in " + activeProfiles[0] + " mode");
        } else {
            logger.info("Running in local mode");
            logger.info("Serving on port 8080");
        }

    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }

}
