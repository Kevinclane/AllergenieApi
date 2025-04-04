package com.allergenie.api.Repos;

import com.allergenie.api.Models.Responses.MenuItemDetails;
import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
import com.allergenie.api.Models.Rows.MenuItemAllergenGroupRow;
import com.allergenie.api.Models.Rows.MenuItemGroupRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MenuItemJdbcRepo implements MenuItemJdbcRepoImpl {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public MenuItemJdbcRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<MenuItemGroupDetails> getGroupedMenuItems(Integer menuId) {
        String query = "SELECT " +
                "mi.id as menuItemId, " +
                "mi.name as menuItemName, " +
                "mi.menu_id as menuId, " +
                "mi.description as menuItemDescription, " +
                "mi.extra_details as menuItemExtraDetails, " +
                "mi.price as menuItemPrice, " +
                "mi.position as menuItemPosition, " +
                "mig.id as groupId, " +
                "mig.name as groupName, " +
                "mig.position as groupPosition " +
                "FROM menu_item mi " +
                "JOIN menu_item_group mig ON mi.menu_item_group_id = mig.id " +
                "WHERE mi.menu_id = :menuId " +
                "ORDER BY groupPosition, menuItemPosition";
        List<MenuItemGroupRow> rows = namedParameterJdbcTemplate.query(
                query,
                new MapSqlParameterSource("menuId", menuId),
                (rs, rn) -> new MenuItemGroupRow(rs)
        );

        Map<Integer, List<MenuItemGroupRow>> groupMap = rows.stream().collect(Collectors.groupingBy(MenuItemGroupRow::getGroupId));

        List<MenuItemGroupDetails> responses = new ArrayList<>();
        for (Map.Entry<Integer, List<MenuItemGroupRow>> entry : groupMap.entrySet()) {
            List<MenuItemGroupRow> groupRows = entry.getValue();

            List<MenuItemDetails> menuItems = new ArrayList<>();
            for (MenuItemGroupRow groupRow : groupRows) {
                MenuItemDetails menuItem = new MenuItemDetails(groupRow);
                menuItems.add(menuItem);
            }

            MenuItemGroupDetails response = new MenuItemGroupDetails(groupRows.get(0), menuItems);
            responses.add(response);
        }

        return responses;
    }

    public List<MenuItemAllergenGroupRow> getMenuItemAllergenGroups(Integer menuId) {
        String query = "SELECT " +
                "mi.id as menuItemId, " +
                "mi.name as menuItemName, " +
                "mi.menu_id as menuId, " +
                "mi.description as menuItemDescription, " +
                "mi.extra_details as menuItemExtraDetails, " +
                "mi.price as menuItemPrice, " +
                "mi.position as menuItemPosition, " +
                "mig.id as groupId, " +
                "mig.name as groupName, " +
                "mig.position as groupPosition, " +
                "mia.id as menuItemAllergenId, " +
                "mia.allergen_id as allergenId " +
                "FROM menu_item mi " +
                "JOIN menu_item_group mig ON mi.menu_item_group_id = mig.id " +
                "JOIN menu_item_allergen mia ON mi.id = mia.menu_item_id " +
                "WHERE mi.menu_id = :menuId " +
                "ORDER BY groupPosition, menuItemPosition";

        List<MenuItemAllergenGroupRow> rows = namedParameterJdbcTemplate.query(
                query,
                new MapSqlParameterSource("menuId", menuId),
                (rs, rn) -> new MenuItemAllergenGroupRow(rs)
        );

        return rows;
    }
}
