package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MenuJdbcRepo implements MenuJdbcRepoImpl {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public MenuJdbcRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Menu> findByRestaurantId(Integer restaurantId) {
        String query = "SELECT m.id, m.name, m.is_active FROM menu m " +
                "JOIN restaurant_menu_crosswalk rmc on m.id = rmc.menu_id " +
                "JOIN restaurant r on r.id = rmc.restaurant_id " +
                "WHERE r.id = :restaurantId;";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("restaurantId", restaurantId);
        List<Menu> responses = namedParameterJdbcTemplate.query(
                query,
                parameters,
                (rs, rn) -> new Menu(rs)
        );

        return responses;
    }
}
