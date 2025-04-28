package com.allergenie.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static java.util.Arrays.asList;

@Repository
public class DatabaseUtils {
    private final JdbcTemplate jdbcTemplate;
    private final List<String> tables = asList(
            "restaurant_menu_crosswalk",
            "restaurant",
            "menu_item_allergen",
            "menu_item",
            "menu_item_group",
            "menu",
            "allergen"
    );

    @Autowired
    public DatabaseUtils(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void resetDatabase() {
        this.tables.stream().forEach(table -> {
            String query = "Delete from " + table;
            jdbcTemplate.update(query);
        });
    }
}
