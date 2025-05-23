package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.*;
import com.allergenie.api.Models.Requests.NewEditMenuRequest;
import com.allergenie.api.Models.Responses.MenuDetailsResponse;
import com.allergenie.api.Models.Responses.MenuItemDetails;
import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
import com.allergenie.api.Repos.MenuJdbcRepo;
import com.allergenie.api.Repos.MenuRepo;
import com.allergenie.api.Repos.RestaurantMenuCrosswalkRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
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
        Integer menuId = null;
        String menuName = request.getName();
        Boolean isActive = request.getIsActive();
        boolean shouldClone = !request.getCloneOptionId().equals(0);

        if (shouldClone) {
            Menu cloneMenu = menuRepo.findById(request.getCloneOptionId())
                    .orElseThrow();
            menuName = cloneMenu.getName();
            isActive = cloneMenu.getIsActive();
            menuId = cloneMenu.getId();
        }

        if (request.getIsLinked()) {
            Menu menu = new Menu();
            menu.setName(menuName);
            menu.setIsActive(isActive);
            menu.setIsLinked(true);
            menuRepo.save(menu);

            if (shouldClone) {
                menuItemService.cloneMenuChildren(menu.getId(), menuId);
            }

            List<RestaurantMenuCrosswalk> crosswalksToSave = new ArrayList<>();
            for (Integer restaurantId : request.getRestaurantIds()) {
                RestaurantMenuCrosswalk crosswalk = new RestaurantMenuCrosswalk();
                crosswalk.setMenuId(menu.getId());
                crosswalk.setRestaurantId(restaurantId);
                crosswalksToSave.add(crosswalk);
            }
            restaurantMenuCrosswalkRepo.saveAll(crosswalksToSave);
            return menu;

        } else {
            List<RestaurantMenuCrosswalk> crosswalksToSave = new ArrayList<>();
            Menu mainMenu = new Menu();

            for (Integer restaurantId : request.getRestaurantIds()) {
                Menu menu = new Menu();
                menu.setName(menuName);
                menu.setIsActive(isActive);
                menu.setIsLinked(false);
                menuRepo.save(menu);

                if (shouldClone) {
                    menuItemService.cloneMenuChildren(menu.getId(), menuId);
                }

                if (restaurantId.equals(request.getBaseRestaurantId())) {
                    mainMenu = menu;
                }

                RestaurantMenuCrosswalk crosswalk = new RestaurantMenuCrosswalk();
                crosswalk.setMenuId(menu.getId());
                crosswalk.setRestaurantId(restaurantId);
                crosswalksToSave.add(crosswalk);
            }

            restaurantMenuCrosswalkRepo.saveAll(crosswalksToSave);

            return mainMenu;
        }
    }

    private List<RestaurantMenuCrosswalk> deleteUnusedCrosswalks(List<RestaurantMenuCrosswalk> crosswalksToSave, List<Integer> restaurantIds) {
        List<RestaurantMenuCrosswalk> crosswalksToDelete = new ArrayList<>();

        for (RestaurantMenuCrosswalk crosswalk : crosswalksToSave) {
            if (!restaurantIds.contains(crosswalk.getRestaurantId())) {
                crosswalksToDelete.add(crosswalk);
            }
        }

        crosswalksToSave.removeAll(crosswalksToDelete);

        restaurantMenuCrosswalkRepo.deleteAll(crosswalksToDelete);
        return crosswalksToSave;
    }

    private void createCrosswalks(List<Integer> existingRestaurantIds, NewEditMenuRequest request, List<RestaurantMenuCrosswalk> crosswalksToSave) {

        for (Integer restaurantId : request.getRestaurantIds()) {
            if (!existingRestaurantIds.contains(restaurantId)) {
                RestaurantMenuCrosswalk newCrosswalk = new RestaurantMenuCrosswalk();
                newCrosswalk.setRestaurantId(restaurantId);

                if (request.getIsLinked()) {
                    newCrosswalk.setMenuId(request.getId());
                } else {
                    Menu newMenu = Menu.builder()
                            .name(request.getName())
                            .isActive(request.getIsActive())
                            .isLinked(request.getIsLinked())
                            .build();
                    menuRepo.save(newMenu);
                    menuItemService.cloneMenuChildren(newMenu.getId(), request.getId());
                    newCrosswalk.setMenuId(newMenu.getId());
                }

                crosswalksToSave.add(newCrosswalk);
            }
        }

        restaurantMenuCrosswalkRepo.saveAll(crosswalksToSave);
    }

    private void updateCrosswalks(List<RestaurantMenuCrosswalk> crosswalksToSave, NewEditMenuRequest request, Boolean previousIsLinked) {
        List<Integer> existingRestaurantIds = crosswalksToSave.stream().map(RestaurantMenuCrosswalk::getRestaurantId).toList();
        crosswalksToSave = deleteUnusedCrosswalks(crosswalksToSave, request.getRestaurantIds());

        if (previousIsLinked && !request.getIsLinked()) {
            for (RestaurantMenuCrosswalk crosswalk : crosswalksToSave) {
                if (!Objects.equals(crosswalk.getRestaurantId(), request.getBaseRestaurantId())) {
                    Menu newMenu = Menu.builder()
                            .name(request.getName())
                            .isActive(request.getIsActive())
                            .isLinked(request.getIsLinked())
                            .build();
                    menuRepo.save(newMenu);
                    menuItemService.cloneMenuChildren(newMenu.getId(), request.getId());
                    crosswalk.setMenuId(newMenu.getId());
                }
            }
        }

        createCrosswalks(existingRestaurantIds, request, crosswalksToSave);
    }

    @Transactional
    public Menu updateMenu(NewEditMenuRequest request) throws Exception {

        Menu existingMenu = menuRepo.findById(request.getId())
                .orElseThrow(() -> new Exception("Menu not found for menuId: " + request.getId()));

        List<RestaurantMenuCrosswalk> crosswalksToSave = restaurantMenuCrosswalkRepo.findByMenuId(request.getId());
        crosswalksToSave = new ArrayList<>(crosswalksToSave);

        if (request.getRestaurantIds().size() == 0) {
            throw new Exception("Restaurant Ids must be provided to update menu");
        }

        Boolean previousIsLinked = existingMenu.getIsLinked();

        existingMenu.setName(request.getName());
        existingMenu.setIsActive(request.getIsActive());
        existingMenu.setIsLinked(request.getIsLinked());
        menuRepo.save(existingMenu);

        updateCrosswalks(crosswalksToSave, request, previousIsLinked);
        return existingMenu;
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
            menuResponse.setIsLinked(menu.get().getIsLinked());

            List<Restaurant> linkedRestaurants = restaurantService.getByMenuId(menuId);
            menuResponse.setLinkedRestaurants(linkedRestaurants);
        }

        return menuResponse;
    }

    private List<MenuItemAllergen> syncMenuItemAllergens(MenuItemDetails mid) {
        List<MenuItemAllergen> response = new ArrayList<>();
        List<MenuItemAllergen> linkedMIAs = new ArrayList<>(allergenService.getAllByMenuItemId(mid.getId()));

        //loop through all allergens in menu item
        for (Allergen a : mid.getAllergens()) {
            MenuItemAllergen mia = linkedMIAs.stream()
                    .filter(x -> x.getAllergenId().equals(a.getId()))
                    .findFirst()
                    .orElse(null);
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
        if (!linkedMIAs.isEmpty()) {
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

        List<Integer> menuItemIds = menuItems.stream().map(MenuItem::getId).toList();
        List<Integer> response = new ArrayList<>(menuItemIds);

        List<MenuItemAllergen> MIAs_toSave = new ArrayList<>();

        //Loop through group's menu items
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItemDetails mid = groupDetails.getMenuItems().get(i);
            mid.setId(menuItemIds.get(i));
            mid.setMenuItemGroupId(groupDetails.getId());
            MIAs_toSave.addAll(syncMenuItemAllergens(mid));
        }
        allergenService.saveMenuItemAllergens(MIAs_toSave);

        return response;
    }

    @Transactional
    public List<MenuItemGroupDetails> updateMenuContents(List<MenuItemGroupDetails> request) {
        List<MenuItemGroup> groups = request.stream().map(MenuItemGroupDetails::getGroup).toList();
        menuItemGroupService.saveGroups(groups);

        List<Integer> menuItemIds_toKeep = new ArrayList<>();

        List<Integer> groupIds_toKeep = groups.stream().map(MenuItemGroup::getId).toList();
        for (int i = 0; i < request.size(); i++) {
            MenuItemGroupDetails details = request.get(i);
            details.setId(groupIds_toKeep.get(i));
            menuItemIds_toKeep.addAll(syncMenuItems(details));
        }

        Integer menuId = request.get(0).getMenuId();
        allergenService.deleteUnusedMenuItemAllergens(menuItemIds_toKeep, menuId);
        menuItemService.deleteUnusedMenuItems(menuItemIds_toKeep, menuId);
        menuItemGroupService.deleteUnusedGroups(groupIds_toKeep, menuId);

        return request;
    }

    public void deleteMenuById(Integer menuId) {
        menuJdbcRepo.deleteMenuAndChildren(menuId);
    }

    @Transactional
    public void deleteByRestaurantId(Integer id) {
        List<Integer> menuIds = menuRepo.findAllByRestaurantId(id);
        for (Integer menuId : menuIds) {
            deleteMenuById(menuId);
        }
    }
}
