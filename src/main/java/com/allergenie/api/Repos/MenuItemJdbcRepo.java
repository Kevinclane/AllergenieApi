package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.Allergen;
import com.allergenie.api.Models.Entities.MenuItem;
import com.allergenie.api.Models.Responses.MenuItemDetails;
import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
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
public class MenuItemJdbcRepo {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public MenuItemJdbcRepo(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public List<MenuItemGroupDetails> getGroupedMenuItems(Integer menuId) {
        String query = "SELECT " +
                "mi.id as menuItemId, " +
                "mi.name as menuItemName, " +
                "mi.menuid as menuId, " +
                "mi.description as menuItemDescription, " +
                "mi.extradetails as menuItemExtraDetails, " +
                "mi.price as menuItemPrice, " +
                "mi.position as menuItemPosition, " +
                "mig.id as groupId, " +
                "mig.name as groupName, " +
                "mig.position as groupPosition " +
                "FROM menuitem mi " +
                "JOIN menuitemgroup mig ON mi.menuitemgroupid = mig.id " +
                "WHERE mi.menuid = :menuId " +
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
}
