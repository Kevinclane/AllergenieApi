package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Models.Entities.RestaurantMenuCrosswalk;
import com.allergenie.api.Models.Requests.NewEditMenuRequest;
import com.allergenie.api.Models.Responses.MenuDetailsResponse;
import com.allergenie.api.Repos.MenuJdbcRepo;
import com.allergenie.api.Repos.MenuRepo;
import com.allergenie.api.Repos.RestaurantMenuCrosswalkRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class MenuService {

    private MenuJdbcRepo menuJdbcRepo;
    private MenuRepo menuRepo;
    private RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo;
    private RestaurantService restaurantService;

    public MenuService(
            MenuJdbcRepo menuJdbcRepo,
            MenuRepo menuRepo,
            RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo,
            RestaurantService restaurantService
    ) {
        this.menuJdbcRepo = menuJdbcRepo;
        this.menuRepo = menuRepo;
        this.restaurantMenuCrosswalkRepo = restaurantMenuCrosswalkRepo;
        this.restaurantService = restaurantService;
    }

    public List<Menu> getMenusByRestaurantId(Integer restaurantId) {
        List<Menu> menus = menuJdbcRepo.findByRestaurantId(restaurantId);
        return menus;
    }

    public Menu createMenu(NewEditMenuRequest request) {
        Menu menu = new Menu(request.getName());
        menuRepo.save(menu);

        for (Integer restaurantId : request.getRestaurantIds()) {
            RestaurantMenuCrosswalk crosswalk = new RestaurantMenuCrosswalk();
            crosswalk.setMenuId(menu.getId());
            crosswalk.setRestaurantId(restaurantId);
            restaurantMenuCrosswalkRepo.save(crosswalk);
        }

        return menu;
    }

    public Menu updateMenu(NewEditMenuRequest request) {
        Menu menu = new Menu(request.getId(), request.getName(), request.getIsActive());
        menuRepo.save(menu);

        List<RestaurantMenuCrosswalk> existingCrosswalks = restaurantMenuCrosswalkRepo.findByMenuId(menu.getId());
        List<RestaurantMenuCrosswalk> shouldDelete = new ArrayList<>();
        List<RestaurantMenuCrosswalk> shouldSave = new ArrayList<>();

        for (RestaurantMenuCrosswalk crosswalk : existingCrosswalks) {
            if (!request.getRestaurantIds().contains(crosswalk.getRestaurantId())) {
                shouldDelete.add(crosswalk);
            }
        }

        for (Integer restaurantId : request.getRestaurantIds()) {
            boolean exists = existingCrosswalks.stream()
                    .anyMatch(crosswalk -> crosswalk.getRestaurantId().equals(restaurantId));
            if (!exists) {
                RestaurantMenuCrosswalk crosswalk = new RestaurantMenuCrosswalk();
                crosswalk.setMenuId(menu.getId());
                crosswalk.setRestaurantId(restaurantId);
                shouldSave.add(crosswalk);
            }
        }

        restaurantMenuCrosswalkRepo.deleteAllInBatch(shouldDelete);
        restaurantMenuCrosswalkRepo.saveAll(shouldSave);

        return menu;
    }

    public MenuDetailsResponse getMenuDetails(Integer menuId) {
        MenuDetailsResponse menuResponse = new MenuDetailsResponse();
        List<Restaurant> allRestaurants = restaurantService.getRestaurants();
        menuResponse.setAllRestaurants(allRestaurants);

        if (menuId != 0) {
            Optional<Menu> menu = menuRepo.findById(menuId);
            if (menu.isEmpty()) {
                throw new RuntimeException("Menu not found for menuId: " + menuId);
            }
            menuResponse.setId(menu.get().getId());
            menuResponse.setName(menu.get().getName());
            menuResponse.setIsActive(menu.get().getIsActive());

            List<Restaurant> linkedRestaurants = restaurantService.getByMenuId(menuId);
            menuResponse.setLinkedRestaurants(linkedRestaurants);
        }

        return menuResponse;
    }


}
