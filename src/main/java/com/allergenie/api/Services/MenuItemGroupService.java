package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.MenuItemGroup;
import com.allergenie.api.Repos.MenuItemGroupRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class MenuItemGroupService {
    private MenuItemGroupRepo menuItemGroupRepo;

    public MenuItemGroupService(MenuItemGroupRepo menuItemGroupRepo) {
        this.menuItemGroupRepo = menuItemGroupRepo;
    }

    public List<MenuItemGroup> getMenuItemGroups(Integer menuId) {
        return menuItemGroupRepo.findAllByMenuId(menuId);
    }

    public List<MenuItemGroup> saveGroups(List<MenuItemGroup> groups) {
        return menuItemGroupRepo.saveAll(groups);
    }

    public void deleteUnusedGroups(List<Integer> existingGroupIds, Integer menuId) {
        menuItemGroupRepo.deleteUnusedGroups(existingGroupIds, menuId);
    }
}
