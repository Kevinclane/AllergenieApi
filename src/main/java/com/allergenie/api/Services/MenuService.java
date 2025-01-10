package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Models.Entities.RestaurantMenuCrosswalk;
import com.allergenie.api.Models.Requests.CreateMenuRequest;
import com.allergenie.api.Repos.MenuJdbcRepo;
import com.allergenie.api.Repos.MenuRepo;
import com.allergenie.api.Repos.RestaurantMenuCrosswalkRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
public class MenuService {

    private MenuJdbcRepo menuJdbcRepo;
    private MenuRepo menuRepo;
    private RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo;

    public MenuService(
            MenuJdbcRepo menuJdbcRepo,
            MenuRepo menuRepo,
            RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo
    ) {
        this.menuJdbcRepo = menuJdbcRepo;
        this.menuRepo = menuRepo;
        this.restaurantMenuCrosswalkRepo = restaurantMenuCrosswalkRepo;
    }

    public List<Menu> getMenusByRestaurantId(Integer restaurantId) {
        List<Menu> menus = menuJdbcRepo.findByRestaurantId(restaurantId);
        return menus;
    }

    public Menu createMenu(CreateMenuRequest request) {
        Menu menu = new Menu(request.getName());
        menuRepo.save(menu);

        for(Integer restaurantId : request.getRestaurantIds()) {
            RestaurantMenuCrosswalk crosswalk = new RestaurantMenuCrosswalk();
            crosswalk.setMenuId(menu.getId());
            crosswalk.setRestaurantId(restaurantId);
            restaurantMenuCrosswalkRepo.save(crosswalk);
        }

        return menu;
    }
}
