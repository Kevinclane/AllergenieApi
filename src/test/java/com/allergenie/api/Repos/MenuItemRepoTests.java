package com.allergenie.api.Repos;

import com.allergenie.api.DatabaseUtils;
import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Models.Entities.MenuItem;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(DatabaseUtils.class)
@DataJpaTest
@Transactional
public class MenuItemRepoTests {
    private DatabaseUtils databaseUtils;
    private MenuRepo menuRepo;
    private MenuItemRepo menuItemRepo;

    @Autowired
    public MenuItemRepoTests(DatabaseUtils databaseUtils, MenuRepo menuRepo, MenuItemRepo menuItemRepo) {
        this.databaseUtils = databaseUtils;
        this.menuRepo = menuRepo;
        this.menuItemRepo = menuItemRepo;
    }

    @BeforeEach
    private void setup() {
        databaseUtils.resetDatabase();
    }

    @Nested
    @DisplayName("findMenuItemIdsToDelete")
    public class FindMenuItemIdsToDelete {
        @Test
        public void shouldReturnListOfIds() {
            Menu firstMenu = Menu.builder()
                    .name("First Menu")
                    .build();
            Menu secondMenu = Menu.builder()
                    .name("Second Menu")
                    .build();
            menuRepo.saveAllAndFlush(asList(firstMenu, secondMenu));

            MenuItem firstItemToKeep = MenuItem.builder()
                    .menuId(firstMenu.getId())
                    .name("FirstKeep")
                    .build();
            MenuItem secondItemToKeep = MenuItem.builder()
                    .menuId(firstMenu.getId())
                    .name("SecondKeep")
                    .build();
            MenuItem thirdItemToKeep = MenuItem.builder()
                    .menuId(secondMenu.getId())
                    .name("ThirdKeep")
                    .build();
            MenuItem firstItemToDelete = MenuItem.builder()
                    .menuId(firstMenu.getId())
                    .name("First Delete")
                    .build();
            MenuItem secondItemToDelete = MenuItem.builder()
                    .menuId(firstMenu.getId())
                    .name("Second Delete")
                    .build();
            menuItemRepo.saveAllAndFlush(asList(
                    firstItemToKeep,
                    secondItemToKeep,
                    thirdItemToKeep,
                    firstItemToDelete,
                    secondItemToDelete
            ));

            List<Integer> expected = asList(firstItemToDelete.getId(), secondItemToDelete.getId());
            List<Integer> actual = menuItemRepo.findMenuItemIdsToDelete(asList(firstItemToKeep.getId(), secondItemToKeep.getId(), thirdItemToKeep.getId()), firstMenu.getId());

            assertEquals(expected, actual);
        }
    }
}
