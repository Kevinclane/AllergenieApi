package com.allergenie.api.Repos;

import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
import com.allergenie.api.Models.Rows.MenuItemAllergenGroupRow;

import java.util.List;

public interface MenuItemJdbcRepoImpl {
    List<MenuItemGroupDetails> getGroupedMenuItems(Integer menuId);
    List<MenuItemAllergenGroupRow> getMenuItemAllergenGroups(Integer originalMenuId);
}
