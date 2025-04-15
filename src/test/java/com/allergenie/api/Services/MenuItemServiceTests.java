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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuItemServiceTests {
    @Mock
    private AllergenService allergenService;
    @Mock
    private MenuItemRepo menuItemRepo;
    @Mock
    private MenuItemJdbcRepo menuItemJdbcRepo;
    @Mock
    private MenuItemGroupRepo menuItemGroupRepo;

    @InjectMocks
    private MenuItemService service;

    Allergen milkAllergen = Allergen.builder()
            .id(1)
            .name("Milk")
            .build();
    Allergen eggAllergen = Allergen.builder()
            .id(2)
            .name("Eggs")
            .build();
    Allergen fishAllergen = Allergen.builder()
            .id(3)
            .name("Fish")
            .build();
    Allergen shellfishAllergen = Allergen.builder()
            .id(4)
            .name("Shellfish")
            .build();
    Allergen treeNutAllergen = Allergen.builder()
            .id(5)
            .name("Tree Nuts")
            .build();
    Allergen peanutAllergen = Allergen.builder()
            .id(6)
            .name("Peanuts")
            .build();
    Allergen wheatAllergen = Allergen.builder()
            .id(7)
            .name("Wheat")
            .build();
    Allergen soyAllergen = Allergen.builder()
            .id(8)
            .name("Soybeans")
            .build();
    Allergen sesameAllergen = Allergen.builder()
            .id(9)
            .name("Sesame")
            .build();

    @Nested
    @DisplayName("getMenuItemsByMenuId")
    public class GetMenuItemsByMenuId {
        @Test
        public void shouldReturnMenuItems() {
            Integer menuId = 99;
            service.getMenuItemsByMenuId(menuId);
            verify(menuItemRepo).findByMenuId(menuId);
        }
    }

    @Nested
    @DisplayName("getMenuItemDetails")
    public class GetMenuItemDetails {
        @Test
        public void shouldGetItemDetails() {
            Integer menuId = 69;

            List<MenuItemGroupDetails> groupDetails = asList(
                    MenuItemGroupDetails.builder()
                            .id(1)
                            .menuId(menuId)
                            .name("Group1")
                            .position(0)
                            .menuItems(asList(
                                    MenuItemDetails.builder()
                                            .id(20)
                                            .menuId(menuId)
                                            .menuItemGroupId(1)
                                            .name("Menu1")
                                            .description("Description1")
                                            .price(9.99)
                                            .position(0)
                                            .build(),
                                    MenuItemDetails.builder()
                                            .id(21)
                                            .menuId(menuId)
                                            .menuItemGroupId(2)
                                            .name("Menu2")
                                            .description("Description2")
                                            .price(2.99)
                                            .position(1)
                                            .build()
                            ))
                            .build(),
                    MenuItemGroupDetails.builder()
                            .id(2)
                            .menuId(menuId)
                            .name("Group2")
                            .position(1)
                            .menuItems(singletonList(
                                    MenuItemDetails.builder()
                                            .id(22)
                                            .menuId(menuId)
                                            .menuItemGroupId(3)
                                            .name("Menu3")
                                            .description("Description3")
                                            .price(0.99)
                                            .position(0)
                                            .build()
                            ))
                            .build()
            );

            when(menuItemJdbcRepo.getGroupedMenuItems(menuId))
                    .thenReturn(groupDetails);

            when(allergenService.getAllergens())
                    .thenReturn(asList(
                            milkAllergen,
                            eggAllergen,
                            shellfishAllergen,
                            treeNutAllergen,
                            peanutAllergen,
                            wheatAllergen,
                            soyAllergen,
                            sesameAllergen
                    ));

            List<MenuItemAllergen> mias = asList(
                    MenuItemAllergen.builder()
                            .id(71)
                            .menuItemId(20)
                            .allergenId(milkAllergen.getId())
                            .build(),
                    MenuItemAllergen.builder()
                            .id(72)
                            .menuItemId(20)
                            .allergenId(peanutAllergen.getId())
                            .build(),
                    MenuItemAllergen.builder()
                            .id(73)
                            .menuItemId(22)
                            .allergenId(soyAllergen.getId())
                            .build()
            );

            when(allergenService.getAllByMenuId(menuId))
                    .thenReturn(mias);

            LoadedMenuResponse expected = LoadedMenuResponse.builder()
                    .allergens(asList(
                            milkAllergen,
                            eggAllergen,
                            shellfishAllergen,
                            treeNutAllergen,
                            peanutAllergen,
                            wheatAllergen,
                            soyAllergen,
                            sesameAllergen
                    ))
                    .groupedItems(asList(MenuItemGroupDetails.builder()
                                    .id(1)
                                    .menuId(menuId)
                                    .name("Group1")
                                    .position(0)
                                    .menuItems(asList(
                                            MenuItemDetails.builder()
                                                    .id(20)
                                                    .menuId(menuId)
                                                    .menuItemGroupId(1)
                                                    .name("Menu1")
                                                    .description("Description1")
                                                    .price(9.99)
                                                    .position(0)
                                                    .allergens(asList(
                                                            milkAllergen,
                                                            peanutAllergen
                                                    ))
                                                    .build(),
                                            MenuItemDetails.builder()
                                                    .id(21)
                                                    .menuId(menuId)
                                                    .menuItemGroupId(2)
                                                    .name("Menu2")
                                                    .description("Description2")
                                                    .price(2.99)
                                                    .position(1)
                                                    .build()
                                    ))
                                    .build(),
                            MenuItemGroupDetails.builder()
                                    .id(2)
                                    .menuId(menuId)
                                    .name("Group2")
                                    .position(1)
                                    .menuItems(singletonList(
                                            MenuItemDetails.builder()
                                                    .id(22)
                                                    .menuId(menuId)
                                                    .menuItemGroupId(3)
                                                    .name("Menu3")
                                                    .description("Description3")
                                                    .price(0.99)
                                                    .position(0)
                                                    .allergens(singletonList(soyAllergen))
                                                    .build()
                                    ))
                                    .build()))
                    .build();

            LoadedMenuResponse actual = service.getMenuItemsDetails(menuId);
            assertEquals(expected, actual);

        }
    }

    @Nested
    @DisplayName("saveMenuItems")
    public class SaveMenuItems {
        @Test
        public void shouldCallMenuItemRepo() {
            List<MenuItem> menuItems = asList(
                    MenuItem.builder()
                            .name("1")
                            .price(2.99)
                            .position(0)
                            .description("1d")
                            .menuItemGroupId(1)
                            .build(),
                    MenuItem.builder()
                            .name("2")
                            .price(3.95)
                            .position(1)
                            .description("2d")
                            .menuItemGroupId(1)
                            .build()
            );
            service.saveMenuItems(menuItems);
            verify(menuItemRepo).saveAll(menuItems);
        }
    }

    @Nested
    @DisplayName("deleteUnusedMenuItems")
    public class DeleteUnusedMenuItems {
        @Test
        public void shouldCallMenuItemRepo() {
            List<Integer> existingMenuIds = asList(1, 5, 6);
            Integer menuId = 9;
            service.deleteUnusedMenuItems(existingMenuIds, menuId);
            verify(menuItemRepo).deleteUnusedMenuItems(existingMenuIds, menuId);
        }
    }

    @Nested
    @DisplayName("cloneMenuChildren")
    public class CloneMenuChildren {
        @Test
        public void shouldSaveCopiesOfMenuItemGroups_MenuItemAllergens_AndMenuItems() {
            //group1 : 2 items
            //  first item: 2 allergens
            //  second item: 0 allergens
            //group2 : 1 item
            //  third item: 1 allergen

            Integer newMenuId = 100;
            Integer originalMenuId = 99;

            List<MenuItemAllergenGroupRow> rows = asList(
                    MenuItemAllergenGroupRow.builder()
                            .menuItemId(11)
                            .menuId(originalMenuId)
                            .menuItemName("FirstItem")
                            .menuItemDescription("FirstDescription")
                            .menuItemExtraDetails("FirstDetails")
                            .menuItemPrice(2.99)
                            .menuItemPosition(0)
                            .groupId(1)
                            .groupName("FirstGroup")
                            .groupPosition(0)
                            .menuItemAllergenId(33)
                            .allergenId(3)
                            .build(),
                    MenuItemAllergenGroupRow.builder()
                            .menuItemId(11)
                            .menuId(originalMenuId)
                            .menuItemName("FirstItem")
                            .menuItemDescription("FirstDescription")
                            .menuItemExtraDetails("FirstDetails")
                            .menuItemPrice(2.99)
                            .menuItemPosition(0)
                            .groupId(1)
                            .groupName("FirstGroup")
                            .groupPosition(0)
                            .menuItemAllergenId(34)
                            .allergenId(4)
                            .build(),
                    MenuItemAllergenGroupRow.builder()
                            .menuItemId(12)
                            .menuId(originalMenuId)
                            .menuItemName("SecondItem")
                            .menuItemDescription("SecondDescription")
                            .menuItemExtraDetails("SecondDetails")
                            .menuItemPrice(1.99)
                            .menuItemPosition(1)
                            .groupId(1)
                            .groupName("FirstGroup")
                            .groupPosition(0)
                            .build(),
                    MenuItemAllergenGroupRow.builder()
                            .menuItemId(13)
                            .menuId(originalMenuId)
                            .menuItemName("ThirdItem")
                            .menuItemDescription("ThirdDescription")
                            .menuItemExtraDetails("ThirdDetails")
                            .menuItemPrice(8.99)
                            .menuItemPosition(0)
                            .groupId(2)
                            .groupName("SecondGroup")
                            .groupPosition(1)
                            .menuItemAllergenId(35)
                            .allergenId(1)
                            .build()
            );

            when(menuItemJdbcRepo.getMenuItemAllergenGroups(originalMenuId))
                    .thenReturn(rows);

            when(menuItemGroupRepo.save(any()))
                    .thenAnswer(args -> {
                        MenuItemGroup mig = args.getArgument(0);
                        mig.setId(200);
                        return mig;
                    }).thenAnswer((args -> {
                        MenuItemGroup mig = args.getArgument(0);
                        mig.setId(201);
                        return mig;
                    }));

            when(menuItemRepo.save(any()))
                    .thenAnswer(args -> {
                        MenuItem mi = args.getArgument(0);
                        mi.setId(20);
                        return mi;
                    }).thenAnswer(args -> {
                        MenuItem mi = args.getArgument(0);
                        mi.setId(21);
                        return mi;
                    }).thenAnswer(args -> {
                        MenuItem mi = args.getArgument(0);
                        mi.setId(22);
                        return mi;
                    });

            service.cloneMenuChildren(newMenuId, originalMenuId);

            verify(menuItemGroupRepo).save(MenuItemGroup.builder()
                    .id(200)
                    .menuId(newMenuId)
                    .name("FirstGroup")
                    .position(0)
                    .build());
            verify(menuItemGroupRepo).save(MenuItemGroup.builder()
                    .id(201)
                    .menuId(newMenuId)
                    .name("SecondGroup")
                    .position(1)
                    .build());

            verify(menuItemRepo).save(MenuItem.builder()
                    .id(20)
                    .menuId(newMenuId)
                    .menuItemGroupId(200)
                    .name("FirstItem")
                    .description("FirstDescription")
                    .extraDetails("FirstDetails")
                    .price(2.99)
                    .position(0)
                    .build());
            verify(menuItemRepo).save(MenuItem.builder()
                    .id(21)
                    .menuId(newMenuId)
                    .menuItemGroupId(200)
                    .name("SecondItem")
                    .description("SecondDescription")
                    .extraDetails("SecondDetails")
                    .price(1.99)
                    .position(1)
                    .build());
            verify(menuItemRepo).save(MenuItem.builder()
                    .id(22)
                    .menuId(newMenuId)
                    .menuItemGroupId(201)
                    .name("ThirdItem")
                    .description("ThirdDescription")
                    .extraDetails("ThirdDetails")
                    .price(8.99)
                    .position(0)
                    .build());

            verify(allergenService).saveMenuItemAllergens(asList(
                    MenuItemAllergen.builder()
                            .allergenId(3)
                            .menuItemId(20)
                            .build(),
                    MenuItemAllergen.builder()
                            .allergenId(4)
                            .menuItemId(20)
                            .build()
            ));
            verify(allergenService).saveMenuItemAllergens(singletonList(
                    MenuItemAllergen.builder()
                            .allergenId(1)
                            .menuItemId(22)
                            .build()
            ));
            verifyNoMoreInteractions(allergenService);
        }
    }
}
