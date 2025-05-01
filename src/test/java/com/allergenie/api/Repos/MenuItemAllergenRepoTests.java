package com.allergenie.api.Repos;

import com.allergenie.api.DatabaseUtils;
import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Models.Entities.MenuItem;
import com.allergenie.api.Models.Entities.MenuItemAllergen;
import com.allergenie.api.Models.Entities.MenuItemGroup;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(DatabaseUtils.class)
@DataJpaTest
@Transactional
public class MenuItemAllergenRepoTests {
    private DatabaseUtils databaseUtils;
    private MenuRepo menuRepo;
    private MenuItemGroupRepo menuItemGroupRepo;
    private MenuItemRepo menuItemRepo;
    private MenuItemAllergenRepo menuItemAllergenRepo;

    @Autowired
    public MenuItemAllergenRepoTests(DatabaseUtils databaseUtils, MenuRepo menuRepo, MenuItemGroupRepo menuItemGroupRepo, MenuItemRepo menuItemRepo, MenuItemAllergenRepo menuItemAllergenRepo) {
        this.databaseUtils = databaseUtils;
        this.menuRepo = menuRepo;
        this.menuItemGroupRepo = menuItemGroupRepo;
        this.menuItemRepo = menuItemRepo;
        this.menuItemAllergenRepo = menuItemAllergenRepo;
    }

    @BeforeEach
    private void setup() {
        databaseUtils.resetDatabase();
    }

    @Nested
    @DisplayName("findAllByMenuId")
    public class FindAllByMenuId {
        @Test
        public void shouldReturnMenuItemAllergensWithMatchingMenuId() {
            Menu firstMenu = Menu.builder()
                    .name("FirstMenu")
                    .isActive(true)
                    .isLinked(false)
                    .build();
            Menu secondMenu = Menu.builder()
                    .name("SecondMenu")
                    .isActive(true)
                    .isLinked(false)
                    .build();
            menuRepo.saveAllAndFlush(asList(firstMenu, secondMenu));

            MenuItemGroup firstGroup = MenuItemGroup.builder()
                    .menuId(firstMenu.getId())
                    .position(0)
                    .name("FirstGroup")
                    .build();
            MenuItemGroup secondGroup = MenuItemGroup.builder()
                    .menuId(secondMenu.getId())
                    .position(0)
                    .name("SecondGroup")
                    .build();
            menuItemGroupRepo.saveAllAndFlush(asList(firstGroup, secondGroup));

            MenuItem firstItem = MenuItem.builder()
                    .menuId(firstMenu.getId())
                    .menuItemGroupId(firstGroup.getId())
                    .price(9.99)
                    .name("FirstItem")
                    .build();
            MenuItem secondItem = MenuItem.builder()
                    .menuId(firstMenu.getId())
                    .menuItemGroupId(firstGroup.getId())
                    .price(2.99)
                    .name("SecondItem")
                    .build();
            MenuItem thirdItem = MenuItem.builder()
                    .menuId(secondMenu.getId())
                    .menuItemGroupId(secondGroup.getId())
                    .price(199.99)
                    .name("ThirdItem")
                    .build();
            menuItemRepo.saveAllAndFlush(asList(firstItem, secondItem, thirdItem));

            MenuItemAllergen firstMia = MenuItemAllergen.builder()
                    .menuItemId(firstItem.getId())
                    .allergenId(2)
                    .build();
            MenuItemAllergen secondMia = MenuItemAllergen.builder()
                    .menuItemId(firstItem.getId())
                    .allergenId(5)
                    .build();
            MenuItemAllergen thirdMia = MenuItemAllergen.builder()
                    .menuItemId(secondItem.getId())
                    .allergenId(1)
                    .build();
            MenuItemAllergen fourthMia = MenuItemAllergen.builder()
                    .menuItemId(thirdItem.getId())
                    .allergenId(4)
                    .build();
            menuItemAllergenRepo.saveAll(asList(firstMia, secondMia, thirdMia, fourthMia));

            List<MenuItemAllergen> actual = menuItemAllergenRepo.findAllByMenuId(firstMenu.getId());
            assertEquals(asList(firstMia, secondMia, thirdMia), actual);
        }
    }

    @Nested
    @DisplayName("findMenuItemAllergenIdsToDelete")
    public class FindMenuItemAllergensIdsToDelete {
        @Test
        public void shouldReturnListOfIds() {
            Menu menu = Menu.builder()
                    .name("Menu")
                    .build();
            menuRepo.saveAndFlush(menu);

            MenuItem firstItem = MenuItem.builder()
                    .name("FirstItem")
                    .menuId(menu.getId())
                    .build();
            MenuItem secondItem = MenuItem.builder()
                    .name("SecondItem")
                    .menuId(menu.getId())
                    .build();
            MenuItem unusedItem = MenuItem.builder()
                    .name("Unused")
                    .menuId(menu.getId())
                    .build();
            menuItemRepo.saveAllAndFlush(asList(firstItem, secondItem, unusedItem));

            MenuItemAllergen firstMiaToKeep = MenuItemAllergen.builder()
                    .allergenId(2)
                    .menuItemId(firstItem.getId())
                    .build();
            MenuItemAllergen secondMiaToKeep = MenuItemAllergen.builder()
                    .allergenId(1)
                    .menuItemId(firstItem.getId())
                    .build();
            MenuItemAllergen thirdMiaToKeep = MenuItemAllergen.builder()
                    .allergenId(5)
                    .menuItemId(secondItem.getId())
                    .build();
            MenuItemAllergen miaToDelete = MenuItemAllergen.builder()
                    .allergenId(5)
                    .menuItemId(unusedItem.getId())
                    .build();
            menuItemAllergenRepo.saveAllAndFlush(asList(firstMiaToKeep, secondMiaToKeep, thirdMiaToKeep, miaToDelete));

            List<Integer> expected = singletonList(miaToDelete.getId());
            List<Integer> actual = menuItemAllergenRepo.findMenuItemAllergenIdsToDelete(asList(firstItem.getId(), secondItem.getId()), menu.getId());
            assertEquals(expected, actual);

        }
    }
}
