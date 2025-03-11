package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.Restaurant;

import java.util.List;

public interface RestaurantJdbcRepoImpl {
    List<Restaurant> getRestaurantsByMenuId(Integer menuId);
}
