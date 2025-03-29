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
        String query = "SELECT m.id, m.name, m.is_active, m.is_linked FROM menu m " +
                "JOIN restaurant_menu_crosswalk rmc on m.id = rmc.menu_id " +
                "JOIN restaurant r on r.id = rmc.restaurant_id " +
                "WHERE r.id = :restaurantId";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("restaurantId", restaurantId);
        List<Menu> responses = namedParameterJdbcTemplate.query(
                query,
                parameters,
                (rs, rn) -> new Menu(rs)
        );

        return responses;
    }

    @Override
    public void deleteMenuAndChildren(Integer menuId) {
        String createTempTable = "CREATE TEMPORARY TABLE temp_ids (id INT PRIMARY KEY)";
        String insertTempData = "INSERT INTO temp_ids (id) SELECT id FROM menu_item WHERE menu_id = :menuId";
        String deleteAllergens = "DELETE FROM menu_item_allergen WHERE menu_item_id IN (SELECT id FROM temp_ids)";
        String deleteMenuItems = "DELETE FROM menu_item WHERE id IN (SELECT id FROM temp_ids)";
        String deleteCrosswalk = "DELETE FROM restaurant_menu_crosswalk WHERE menu_id = :menuId";
        String deleteMenu = "DELETE FROM menu WHERE id = :menuId";
        String dropTempTable = "DROP TEMPORARY TABLE IF EXISTS temp_ids";

        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue("menuId", menuId);

        namedParameterJdbcTemplate.getJdbcTemplate().update(createTempTable);
        namedParameterJdbcTemplate.update(insertTempData, parameterSource);
        namedParameterJdbcTemplate.getJdbcTemplate().update(deleteAllergens);
        namedParameterJdbcTemplate.getJdbcTemplate().update(deleteMenuItems);
        namedParameterJdbcTemplate.update(deleteCrosswalk, parameterSource);
        namedParameterJdbcTemplate.update(deleteMenu, parameterSource);
        namedParameterJdbcTemplate.getJdbcTemplate().update(dropTempTable);
    }
}
