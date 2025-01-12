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
        String query = "SELECT r.id, r.name, r.phoneNumber, r.emailAddress, r.streetAddress, r.streetAddressTwo, r.city, r.state, r.zipCode FROM Restaurant r " +
                "JOIN RestaurantMenuCrosswalk rmc ON r.id = rmc.restaurantId " +
                "WHERE rmc.menuId = :menuId;";
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
