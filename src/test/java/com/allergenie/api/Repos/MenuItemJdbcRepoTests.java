package com.allergenie.api.Repos;

import com.allergenie.api.AllergenieApiApplication;
import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Models.Entities.MenuItem;
import com.allergenie.api.Models.Entities.MenuItemAllergen;
import com.allergenie.api.Models.Entities.MenuItemGroup;
import com.allergenie.api.Models.Responses.MenuItemDetails;
import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
import com.allergenie.api.Models.Rows.MenuItemAllergenGroupRow;
import com.allergenie.api.DatabaseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(DatabaseUtils.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AllergenieApiApplication.class))
public class MenuItemJdbcRepoTests {
    private DatabaseUtils databaseUtils;
    private MenuRepo menuRepo;
    private MenuItemGroupRepo menuItemGroupRepo;
    private MenuItemRepo menuItemRepo;
    private MenuItemAllergenRepo menuItemAllergenRepo;
    private MenuItemJdbcRepo menuItemJdbcRepo;

    @Autowired
    public MenuItemJdbcRepoTests(DatabaseUtils databaseUtils, MenuRepo menuRepo, MenuItemGroupRepo menuItemGroupRepo, MenuItemRepo menuItemRepo, MenuItemAllergenRepo menuItemAllergenRepo, MenuItemJdbcRepo menuItemJdbcRepo) {
        this.databaseUtils = databaseUtils;
        this.menuRepo = menuRepo;
        this.menuItemGroupRepo = menuItemGroupRepo;
        this.menuItemRepo = menuItemRepo;
        this.menuItemAllergenRepo = menuItemAllergenRepo;
        this.menuItemJdbcRepo = menuItemJdbcRepo;
    }

    @BeforeEach
    public void setup() {
        databaseUtils.resetDatabase();
    }

    @Nested
    @DisplayName("getGroupedMenuItems")
    public class GetGroupedMenuItems {
        @Test
        public void shouldReturnListOfMenuItemGroupDetails() {
            Menu menu = Menu.builder()
                    .name("Menu")
                    .build();
            menuRepo.saveAndFlush(menu);

            MenuItemGroup firstGroup = MenuItemGroup.builder()
                    .menuId(menu.getId())
                    .name("First Group")
                    .position(0)
                    .build();
            MenuItemGroup secondGroup = MenuItemGroup.builder()
                    .menuId(menu.getId())
                    .name("Second Group")
                    .position(1)
                    .build();
            menuItemGroupRepo.saveAllAndFlush(asList(firstGroup, secondGroup));

            MenuItem firstItem = MenuItem.builder()
                    .menuId(menu.getId())
                    .menuItemGroupId(firstGroup.getId())
                    .name("FirstItem")
                    .price(7.99)
                    .position(0)
                    .build();
            MenuItem secondItem = MenuItem.builder()
                    .menuId(menu.getId())
                    .menuItemGroupId(firstGroup.getId())
                    .name("SecondItem")
                    .price(12.00)
                    .position(1)
                    .build();
            MenuItem thirdItem = MenuItem.builder()
                    .menuId(menu.getId())
                    .menuItemGroupId(firstGroup.getId())
                    .name("ThirdItem")
                    .position(2)
                    .price(2.50)
                    .build();
            MenuItem fourthItem = MenuItem.builder()
                    .menuId(menu.getId())
                    .menuItemGroupId(secondGroup.getId())
                    .name("FourthItem")
                    .price(3.49)
                    .position(0)
                    .build();
            menuItemRepo.saveAllAndFlush(asList(firstItem, secondItem, thirdItem, fourthItem));

            List<MenuItemGroupDetails> expected = asList(
                    MenuItemGroupDetails.builder()
                            .id(firstGroup.getId())
                            .menuId(menu.getId())
                            .name(firstGroup.getName())
                            .position(firstGroup.getPosition())
                            .menuItems(asList(
                                    MenuItemDetails.builder()
                                            .id(firstItem.getId())
                                            .menuId(firstItem.getMenuId())
                                            .menuItemGroupId(firstItem.getMenuItemGroupId())
                                            .name(firstItem.getName())
                                            .price(firstItem.getPrice())
                                            .position(firstItem.getPosition())
                                            .build(),
                                    MenuItemDetails.builder()
                                            .id(secondItem.getId())
                                            .menuId(secondItem.getMenuId())
                                            .menuItemGroupId(secondItem.getMenuItemGroupId())
                                            .name(secondItem.getName())
                                            .price(secondItem.getPrice())
                                            .position(secondItem.getPosition())
                                            .build(),
                                    MenuItemDetails.builder()
                                            .id(thirdItem.getId())
                                            .menuId(thirdItem.getMenuId())
                                            .menuItemGroupId(thirdItem.getMenuItemGroupId())
                                            .name(thirdItem.getName())
                                            .price(thirdItem.getPrice())
                                            .position(thirdItem.getPosition())
                                            .build()
                            ))
                            .build(),
                    MenuItemGroupDetails.builder()
                            .id(secondGroup.getId())
                            .menuId(menu.getId())
                            .name(secondGroup.getName())
                            .position(secondGroup.getPosition())
                            .menuItems(singletonList(
                                    MenuItemDetails.builder()
                                            .id(fourthItem.getId())
                                            .menuId(fourthItem.getMenuId())
                                            .menuItemGroupId(fourthItem.getMenuItemGroupId())
                                            .name(fourthItem.getName())
                                            .price(fourthItem.getPrice())
                                            .position(fourthItem.getPosition())
                                            .build()
                            ))
                            .build()
            );
            List<MenuItemGroupDetails> actual = menuItemJdbcRepo.getGroupedMenuItems(menu.getId());

            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("getMenuItemAllergenGroups")
    public class GetMenuItemAllergenGroups {
        @Test
        public void shouldReturnListOfMenuItemAllergenGroupRows() {
            Menu menu = Menu.builder()
                    .name("Menu")
                    .build();
            menuRepo.saveAndFlush(menu);

            MenuItemGroup firstGroup = MenuItemGroup.builder()
                    .menuId(menu.getId())
                    .name("FirstGroup")
                    .position(0)
                    .build();
            MenuItemGroup secondGroup = MenuItemGroup.builder()
                    .menuId(menu.getId())
                    .name("SecondGroup")
                    .position(1)
                    .build();
            menuItemGroupRepo.saveAllAndFlush(asList(firstGroup, secondGroup));

            MenuItem firstItem = MenuItem.builder()
                    .menuId(menu.getId())
                    .menuItemGroupId(firstGroup.getId())
                    .name("FirstItem")
                    .description("FirstDescription")
                    .extraDetails("FirstED")
                    .price(7.99)
                    .position(0)
                    .build();
            MenuItem secondItem = MenuItem.builder()
                    .menuId(menu.getId())
                    .menuItemGroupId(firstGroup.getId())
                    .name("SecondItem")
                    .description("SecondDescription")
                    .extraDetails("SecondED")
                    .price(12.00)
                    .position(1)
                    .build();
            MenuItem thirdItem = MenuItem.builder()
                    .menuId(menu.getId())
                    .menuItemGroupId(firstGroup.getId())
                    .name("ThirdItem")
                    .description("ThirdDescription")
                    .extraDetails("ThirdED")
                    .position(2)
                    .price(2.50)
                    .build();
            MenuItem fourthItem = MenuItem.builder()
                    .menuId(menu.getId())
                    .menuItemGroupId(secondGroup.getId())
                    .name("FourthItem")
                    .description("FourthDescription")
                    .extraDetails("FourthED")
                    .price(3.49)
                    .position(0)
                    .build();
            menuItemRepo.saveAllAndFlush(asList(firstItem, secondItem, thirdItem, fourthItem));

            MenuItemAllergen firstAllergen = MenuItemAllergen.builder()
                    .menuItemId(firstItem.getId())
                    .allergenId(2)
                    .build();
            MenuItemAllergen secondAllergen = MenuItemAllergen.builder()
                    .menuItemId(firstItem.getId())
                    .allergenId(5)
                    .build();
            MenuItemAllergen thirdAllergen = MenuItemAllergen.builder()
                    .menuItemId(fourthItem.getId())
                    .allergenId(6)
                    .build();
            menuItemAllergenRepo.saveAllAndFlush(asList(firstAllergen, secondAllergen, thirdAllergen));

            List<MenuItemAllergenGroupRow> expected = asList(
                    MenuItemAllergenGroupRow.builder()
                            .menuItemId(firstItem.getId())
                            .menuId(menu.getId())
                            .menuItemName(firstItem.getName())
                            .menuItemDescription(firstItem.getDescription())
                            .menuItemExtraDetails(firstItem.getExtraDetails())
                            .menuItemPrice(firstItem.getPrice())
                            .menuItemPosition(firstItem.getPosition())
                            .groupId(firstGroup.getId())
                            .groupName(firstGroup.getName())
                            .groupPosition(firstGroup.getPosition())
                            .menuItemAllergenId(firstAllergen.getId())
                            .allergenId(firstAllergen.getAllergenId())
                            .build(),
                    MenuItemAllergenGroupRow.builder()
                            .menuItemId(firstItem.getId())
                            .menuId(menu.getId())
                            .menuItemName(firstItem.getName())
                            .menuItemDescription(firstItem.getDescription())
                            .menuItemExtraDetails(firstItem.getExtraDetails())
                            .menuItemPrice(firstItem.getPrice())
                            .menuItemPosition(firstItem.getPosition())
                            .groupId(firstGroup.getId())
                            .groupName(firstGroup.getName())
                            .groupPosition(firstGroup.getPosition())
                            .menuItemAllergenId(secondAllergen.getId())
                            .allergenId(secondAllergen.getAllergenId())
                            .build(),
                    MenuItemAllergenGroupRow.builder()
                            .menuItemId(fourthItem.getId())
                            .menuId(menu.getId())
                            .menuItemName(fourthItem.getName())
                            .menuItemDescription(fourthItem.getDescription())
                            .menuItemExtraDetails(fourthItem.getExtraDetails())
                            .menuItemPrice(fourthItem.getPrice())
                            .menuItemPosition(fourthItem.getPosition())
                            .groupId(secondGroup.getId())
                            .groupName(secondGroup.getName())
                            .groupPosition(secondGroup.getPosition())
                            .menuItemAllergenId(thirdAllergen.getId())
                            .allergenId(thirdAllergen.getAllergenId())
                            .build()
            );

            List<MenuItemAllergenGroupRow> actual = menuItemJdbcRepo.getMenuItemAllergenGroups(menu.getId());
            assertEquals(expected, actual);
        }
    }
}
