package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Allergen;
import com.allergenie.api.Models.Entities.MenuItem;
import com.allergenie.api.Models.Entities.MenuItemAllergen;
import com.allergenie.api.Models.Responses.MenuItemDetails;
import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
import com.allergenie.api.Models.Responses.LoadedMenuResponse;
import com.allergenie.api.Models.Rows.MenuItemGroupRow;
import com.allergenie.api.Repos.MenuItemJdbcRepo;
import com.allergenie.api.Repos.MenuItemRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemService {
    private MenuItemRepo menuItemRepo;
    private AllergenService allergenService;
    private MenuItemJdbcRepo menuItemJdbcRepo;

    public MenuItemService(
            MenuItemRepo menuItemRepo,
            AllergenService allergenService,
            MenuItemJdbcRepo menuItemJdbcRepo
    ) {
        this.menuItemRepo = menuItemRepo;
        this.allergenService = allergenService;
        this.menuItemJdbcRepo = menuItemJdbcRepo;
    }

    public List<MenuItem> getMenuItems(Integer menuId) {
        return menuItemRepo.findByMenuId(menuId);
    }

    public LoadedMenuResponse getMenuItemsDetails(Integer menuId) {
        LoadedMenuResponse response = new LoadedMenuResponse();
        List<MenuItemGroupDetails> responses = menuItemJdbcRepo.getGroupedMenuItems(menuId);

        List<Allergen> allergens = allergenService.getAllergens();

        List<MenuItemAllergen> allLinkedAllergens = allergenService.getAllByMenuId(menuId);
        Map<Integer, List<MenuItemAllergen>> allergenMap = allLinkedAllergens.stream().collect(Collectors.groupingBy(MenuItemAllergen::getMenuItemId));

        for (MenuItemGroupDetails group : responses) {
            for (MenuItemDetails item : group.getMenuItems()) {
                List<MenuItemAllergen> linkedAllergens = allergenMap.get(item.getId());
                if(linkedAllergens == null) {
                    continue;
                }
                List<Allergen> a = new ArrayList<>();

                for(MenuItemAllergen mia : linkedAllergens) {
                    a = allergenService.getAllergens().stream().filter(x -> Objects.equals(x.getId(), mia.getAllergenId())).collect(Collectors.toList());
                }
                item.setAllergens(a);
            }
        }

        response.setGroupedItems(responses);
        response.setAllergens(allergens);
        return response;
    }

    public void saveMenuItems(List<MenuItem> menuItems) {
        menuItemRepo.saveAll(menuItems);
    }

    public void deleteUnusedMenuItems(List<Integer> existingMenuItemIds, Integer menuId) {
        menuItemRepo.deleteUnusedMenuItems(existingMenuItemIds, menuId);
    }
}
