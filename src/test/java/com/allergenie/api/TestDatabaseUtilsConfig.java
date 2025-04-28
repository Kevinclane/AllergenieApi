package com.allergenie.api;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@TestConfiguration
public class TestDatabaseUtilsConfig {
    @Bean
    public DatabaseUtils databaseUtils(JdbcTemplate jdbcTemplate) {
        return new DatabaseUtils(jdbcTemplate);
    }
}
