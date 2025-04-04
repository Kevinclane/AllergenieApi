package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.Menu;

import java.util.List;

public interface MenuJdbcRepoImpl {
    List<Menu> findByRestaurantId(Integer restaurantId);

    void deleteMenuAndChildren(Integer menuId);
}
