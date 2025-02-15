package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RestaurantJdbRepo {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public RestaurantJdbRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<Restaurant> getRestaurantsByMenuId(Integer menuId) {
        String query = "SELECT r.id, r.name, r.phone_number, r.email_address, r.street_address, r.street_address_two, r.city, r.state, r.zip_code FROM restaurant r " +
                "JOIN restaurant_menu_crosswalk rmc ON r.id = rmc.restaurant_id " +
                "WHERE rmc.menu_id = :menuId;";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("menuId", menuId);

        List<Restaurant> responses = namedParameterJdbcTemplate.query(
                query,
                parameters,
                (rs, rn) -> new Restaurant(rs)
        );

        return responses;
    }
}
