package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.*;
import com.allergenie.api.Models.Requests.NewEditMenuRequest;
import com.allergenie.api.Models.Responses.MenuDetailsResponse;
import com.allergenie.api.Models.Responses.MenuItemDetails;
import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
import com.allergenie.api.Repos.MenuJdbcRepo;
import com.allergenie.api.Repos.MenuRepo;
import com.allergenie.api.Repos.RestaurantMenuCrosswalkRepo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

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
    private MenuItemGroupService menuItemGroupService;
    private MenuItemService menuItemService;
    private AllergenService allergenService;

    public MenuService(
            MenuJdbcRepo menuJdbcRepo,
            MenuRepo menuRepo,
            RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo,
            RestaurantService restaurantService,
            MenuItemGroupService menuItemGroupService,
            MenuItemService menuItemService,
            AllergenService allergenService
    ) {
        this.menuJdbcRepo = menuJdbcRepo;
        this.menuRepo = menuRepo;
        this.restaurantMenuCrosswalkRepo = restaurantMenuCrosswalkRepo;
        this.restaurantService = restaurantService;
        this.menuItemGroupService = menuItemGroupService;
        this.menuItemService = menuItemService;
        this.allergenService = allergenService;
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

    private List<MenuItemAllergen> syncMenuItemAllergens(MenuItemDetails mid) {
        List<MenuItemAllergen> response = new ArrayList<>();
        List<MenuItemAllergen> linkedMIAs = allergenService.getAllByMenuItemId(mid.getId());

        //loop through all allergens in request
        for (Allergen a : mid.getAllergens()) {
            MenuItemAllergen mia = linkedMIAs.stream().anyMatch(x -> x.getAllergenId().equals(a.getId())) ? linkedMIAs.stream().filter(x -> x.getAllergenId().equals(a.getId())).findFirst().get() : null;
            if (mia != null) {
                //crosswalk already exists, remove from list so remainder can be deleted
                linkedMIAs.remove(mia);
            } else {
                //crosswalk does not exist, add to list to be created
                mia = new MenuItemAllergen();
                mia.setAllergenId(a.getId());
                mia.setMenuItemId(mid.getId());
                response.add(mia);
            }
        }

        //delete all remaining linked allergens
        if(!linkedMIAs.isEmpty()) {
            allergenService.deleteMenuItemAllergens(linkedMIAs);
        }

        return response;
    }

    private List<Integer> syncMenuItems(MenuItemGroupDetails groupDetails) {

        List<MenuItem> menuItems = groupDetails.getMenuItems().stream().map(menuItemDetails -> {
            MenuItem mi = new MenuItem(menuItemDetails);
            mi.setMenuItemGroupId(groupDetails.getId());
            return mi;
        }).toList();
        menuItemService.saveMenuItems(menuItems);

        List<Integer> ids = menuItems.stream().map(MenuItem::getId).toList();
        List<Integer> response = new ArrayList<>(ids);

        List<MenuItemAllergen> MIAs_toSave = new ArrayList<>();

        //Loop through group's menu items
        for (int i = 0; i < groupDetails.getMenuItems().size(); i++) {
            MenuItemDetails mid = groupDetails.getMenuItems().get(i);
            mid.setId(ids.get(i));
            mid.setMenuItemGroupId(groupDetails.getId());
            MIAs_toSave.addAll(syncMenuItemAllergens(mid));
        }
        allergenService.saveMenuItemAllergens(MIAs_toSave);

        return response;
    }


    @Transactional
    public List<MenuItemGroupDetails> updateFullMenu(List<MenuItemGroupDetails> request) {
        Integer menuId = request.get(0).getMenuId();
        List<MenuItemGroup> groups = new ArrayList<>();
        for (MenuItemGroupDetails groupResponse : request) {
            MenuItemGroup group = new MenuItemGroup();
            group.setId(groupResponse.getId() == 0 ? null : groupResponse.getId());
            group.setMenuId(groupResponse.getMenuId());
            group.setName(groupResponse.getName());
            group.setPosition(groupResponse.getPosition());
            groups.add(group);
        }

        menuItemGroupService.saveGroups(groups);

        List<Integer> groupIds_toKeep = groups.stream().map(MenuItemGroup::getId).toList();
        List<Integer> menuItemIds_toKeep = new ArrayList<>();

        for (int i = 0; i < request.size(); i++) {
            MenuItemGroupDetails groupDetails = request.get(i);
            groupDetails.setId(groupIds_toKeep.get(i));
            menuItemIds_toKeep.addAll(syncMenuItems(groupDetails));
        }

        //add cascading delete
        allergenService.deleteUnusedMenuItemAllergens(menuItemIds_toKeep, menuId);
        menuItemService.deleteUnusedMenuItems(menuItemIds_toKeep, menuId);

        //add cascading delete?
        menuItemGroupService.deleteUnusedGroups(groupIds_toKeep, menuId);

        return request;
    }


}
