package com.allergenie.api.Repos;

import com.allergenie.api.Models.Responses.MenuItemGroupDetails;

import java.util.List;

public interface MenuItemJdbcRepoImpl {
    List<MenuItemGroupDetails> getGroupedMenuItems(Integer menuId);
}
