package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Allergen;
import com.allergenie.api.Models.Entities.MenuItem;
import com.allergenie.api.Models.Entities.MenuItemAllergen;
import com.allergenie.api.Models.Entities.MenuItemGroup;
import com.allergenie.api.Models.Responses.LoadedMenuResponse;
import com.allergenie.api.Models.Responses.MenuItemDetails;
import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
import com.allergenie.api.Models.Rows.MenuItemAllergenGroupRow;
import com.allergenie.api.Repos.MenuItemGroupRepo;
import com.allergenie.api.Repos.MenuItemJdbcRepo;
import com.allergenie.api.Repos.MenuItemRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MenuItemService {
    private AllergenService allergenService;
    private MenuItemRepo menuItemRepo;
    private MenuItemJdbcRepo menuItemJdbcRepo;
    private MenuItemGroupRepo menuItemGroupRepo;

    public MenuItemService(
            AllergenService allergenService,
            MenuItemRepo menuItemRepo,
            MenuItemJdbcRepo menuItemJdbcRepo,
            MenuItemGroupRepo menuItemGroupRepo
    ) {
        this.allergenService = allergenService;
        this.menuItemRepo = menuItemRepo;
        this.menuItemJdbcRepo = menuItemJdbcRepo;
        this.menuItemGroupRepo = menuItemGroupRepo;
    }

    public List<MenuItem> getMenuItemsByMenuId(Integer menuId) {
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
                if (linkedAllergens == null) {
                    continue;
                }
                List<Allergen> la = new ArrayList<>();

                for (MenuItemAllergen mia : linkedAllergens) {
                    Optional<Allergen> a = allergens.stream().filter(x -> x.getId().equals(mia.getAllergenId())).findFirst();
                    a.ifPresent(la::add);
                }
                item.setAllergens(la);
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
        List<Integer> idToDelete = menuItemRepo.findMenuItemIdsToDelete(existingMenuItemIds, menuId);
        if (idToDelete.size() > 0) {
            menuItemRepo.deleteByIdIn(idToDelete);
        }
    }

    public void cloneMenuChildren(Integer newMenuId, Integer originalMenuId) {
        List<MenuItemAllergenGroupRow> rows = menuItemJdbcRepo.getMenuItemAllergenGroups(originalMenuId);
        Map<Integer, List<MenuItemAllergenGroupRow>> groupMap = rows.stream().collect(Collectors.groupingBy(MenuItemAllergenGroupRow::getGroupId));

        for (Map.Entry<Integer, List<MenuItemAllergenGroupRow>> entry : groupMap.entrySet()) {
            List<MenuItemAllergenGroupRow> groupRows = entry.getValue(); //all rows with same group

            MenuItemGroup group = new MenuItemGroup(groupRows.get(0));
            group.setId(null);
            group.setMenuId(newMenuId);
            menuItemGroupRepo.save(group);

            Map<Integer, List<MenuItemAllergenGroupRow>> itemMap = groupRows.stream().collect(Collectors.groupingBy(MenuItemAllergenGroupRow::getMenuItemId));

            for (Map.Entry<Integer, List<MenuItemAllergenGroupRow>> entry2 : itemMap.entrySet()) {
                List<MenuItemAllergenGroupRow> itemRows = entry2.getValue(); //all rows with same item

                MenuItem menuItem = new MenuItem(itemRows.get(0));
                menuItem.setId(null);
                menuItem.setMenuId(newMenuId);
                menuItem.setMenuItemGroupId(group.getId());
                menuItemRepo.save(menuItem);

                List<MenuItemAllergen> allergens = new ArrayList<>();
                for (MenuItemAllergenGroupRow r : itemRows) {
                    if (r.getAllergenId() == null) {
                        continue;
                    }
                    MenuItemAllergen a = new MenuItemAllergen();
                    a.setMenuItemId(menuItem.getId());
                    a.setAllergenId(r.getAllergenId());
                    allergens.add(a);
                }
                if (allergens.size() > 0) {
                    allergenService.saveMenuItemAllergens(allergens);
                }
            }

        }
    }
}
