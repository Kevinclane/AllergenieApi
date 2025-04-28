package com.allergenie.api.Repos;

import com.allergenie.api.DatabaseUtils;
import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Models.Entities.MenuItemGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(DatabaseUtils.class)
@DataJpaTest
public class MenuItemGroupRepoTests {
    private DatabaseUtils databaseUtils;
    private MenuItemGroupRepo menuItemGroupRepo;
    private MenuRepo menuRepo;

    @Autowired
    public MenuItemGroupRepoTests(DatabaseUtils databaseUtils, MenuItemGroupRepo menuItemGroupRepo, MenuRepo menuRepo) {
        this.databaseUtils = databaseUtils;
        this.menuItemGroupRepo = menuItemGroupRepo;
        this.menuRepo = menuRepo;
    }

    @BeforeEach
    private void setup() {
        databaseUtils.resetDatabase();
    }

    @Nested
    @DisplayName("deleteUnusedGroups")
    public class DeleteUnusedGroups {
        @Test
        public void shouldDeleteMenuItemGroupsNotLinkedToProvidedIds() {
            Menu menu = Menu.builder()
                    .name("Menu")
                    .build();
            menuRepo.saveAndFlush(menu);

            MenuItemGroup firstGroupToKeep = MenuItemGroup.builder()
                    .name("FirstGroup")
                    .menuId(menu.getId())
                    .build();
            MenuItemGroup secondGroupToKeep = MenuItemGroup.builder()
                    .name("SecondGroup")
                    .menuId(menu.getId())
                    .build();
            MenuItemGroup firstGroupToDelete = MenuItemGroup.builder()
                    .name("FirstToDelete")
                    .menuId(menu.getId())
                    .build();
            MenuItemGroup secondGroupToDelete = MenuItemGroup.builder()
                    .name("SecondToDelete")
                    .menuId(menu.getId())
                    .build();
            menuItemGroupRepo.saveAllAndFlush(asList(firstGroupToKeep, secondGroupToKeep, firstGroupToDelete, secondGroupToDelete));

            menuItemGroupRepo.deleteUnusedGroups(asList(firstGroupToKeep.getId(), secondGroupToKeep.getId()), menu.getId());

            List<MenuItemGroup> expected = asList(firstGroupToKeep, secondGroupToKeep);
            List<MenuItemGroup> actual = menuItemGroupRepo.findAll();
            assertEquals(expected, actual);
        }
    }
}
