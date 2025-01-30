package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Allergen;
import com.allergenie.api.Models.Entities.MenuItemAllergen;
import com.allergenie.api.Repos.AllergenRepo;
import com.allergenie.api.Repos.MenuItemAllergenRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AllergenService {
    private AllergenRepo allergenRepo;
    private MenuItemAllergenRepo menuItemAllergenRepo;

    public AllergenService(
            AllergenRepo allergenRepo,
            MenuItemAllergenRepo menuItemAllergenRepo
    ) {
        this.allergenRepo = allergenRepo;
        this.menuItemAllergenRepo = menuItemAllergenRepo;
    }

    public List<Allergen> getAllergens() {
        return allergenRepo.findAll();
    }

    public List<MenuItemAllergen> getAllByMenuId(Integer menuId) {
        return menuItemAllergenRepo.findAllByMenuId(menuId);
    }

    public List<MenuItemAllergen> getAllByMenuItemId(Integer menuItemId) {
        return menuItemAllergenRepo.findAllByMenuItemId(menuItemId);
    }

    public void saveMenuItemAllergens(List<MenuItemAllergen> miAsToKeep) {
        menuItemAllergenRepo.saveAll(miAsToKeep);
    }

    public void deleteMenuItemAllergens(List<MenuItemAllergen> linkedMIAs) {
        menuItemAllergenRepo.deleteAll(linkedMIAs);
    }

    public void deleteUnusedMenuItemAllergens(List<Integer> menuItemIds, Integer menuId) {
        menuItemAllergenRepo.deleteUnusedMenuItemAllergens(menuItemIds, menuId);
    }
}
