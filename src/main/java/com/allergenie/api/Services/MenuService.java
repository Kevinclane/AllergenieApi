package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Repos.MenuJdbcRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {

    private MenuJdbcRepo menuJdbcRepo;

    public MenuService(MenuJdbcRepo menuJdbcRepo) {
        this.menuJdbcRepo = menuJdbcRepo;
    }

    public List<Menu> getMenusByRestaurantId(Integer restaurantId) {
        List<Menu> menus = menuJdbcRepo.findByRestaurantId(restaurantId);
        return menus;
    }

}
