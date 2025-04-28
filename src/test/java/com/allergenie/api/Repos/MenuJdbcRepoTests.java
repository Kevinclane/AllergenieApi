package com.allergenie.api.Repos;

import com.allergenie.api.AllergenieApiApplication;
import com.allergenie.api.DatabaseUtils;
import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Models.Entities.RestaurantMenuCrosswalk;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import(DatabaseUtils.class)
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AllergenieApiApplication.class))
public class MenuJdbcRepoTests {
    private DatabaseUtils databaseUtils;
    private MenuRepo menuRepo;
    private RestaurantRepo restaurantRepo;
    private RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo;
    private MenuJdbcRepo menuJdbcRepo;

    @Autowired
    public MenuJdbcRepoTests(
            DatabaseUtils databaseUtils,
            MenuRepo menuRepo,
            RestaurantRepo restaurantRepo,
            RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo,
            MenuJdbcRepo menuJdbcRepo
    ) {
        this.databaseUtils = databaseUtils;
        this.menuRepo = menuRepo;
        this.restaurantRepo = restaurantRepo;
        this.restaurantMenuCrosswalkRepo = restaurantMenuCrosswalkRepo;
        this.menuJdbcRepo = menuJdbcRepo;
    }

    @BeforeEach
    public void setup() {
        databaseUtils.resetDatabase();
    }

    @Nested
    @DisplayName("findByRestaurantId")
    public class FindByRestaurantId {
        @Test
        public void shouldReturnListOfMenusLinkedToProvidedRestaurantId() {
            Restaurant firstRestaurant = Restaurant.builder()
                    .name("FirstRestaurant")
                    .build();
            Restaurant secondRestaurant = Restaurant.builder()
                    .name("SecondRestaurant")
                    .build();
            restaurantRepo.saveAllAndFlush(asList(firstRestaurant, secondRestaurant));

            Menu firstMenu = Menu.builder()
                    .name("FirstMenu")
                    .isActive(false)
                    .isLinked(false)
                    .build();
            Menu secondMenu = Menu.builder()
                    .name("SecondMenu")
                    .isActive(false)
                    .isLinked(false)
                    .build();
            Menu thirdMenu = Menu.builder()
                    .name("ThirdMenu")
                    .isActive(false)
                    .isLinked(false)
                    .build();
            menuRepo.saveAllAndFlush(asList(firstMenu, secondMenu, thirdMenu));

            RestaurantMenuCrosswalk firstCrosswalk = RestaurantMenuCrosswalk.builder()
                    .menuId(firstMenu.getId())
                    .restaurantId(firstRestaurant.getId())
                    .build();
            RestaurantMenuCrosswalk secondCrosswalk = RestaurantMenuCrosswalk.builder()
                    .menuId(secondMenu.getId())
                    .restaurantId(firstRestaurant.getId())
                    .build();
            RestaurantMenuCrosswalk thirdCrosswalk = RestaurantMenuCrosswalk.builder()
                    .menuId(thirdMenu.getId())
                    .restaurantId(secondRestaurant.getId())
                    .build();
            restaurantMenuCrosswalkRepo.saveAllAndFlush(asList(firstCrosswalk, secondCrosswalk, thirdCrosswalk));

            List<Menu> expected = asList(firstMenu, secondMenu);
            List<Menu> actual = menuJdbcRepo.findByRestaurantId(firstRestaurant.getId());
            assertEquals(expected, actual);
        }
    }


//    !NOTE - Not tested due to H2 database not supporting temporary tables
//    @Nested
//    @DisplayName("deleteMenuAndChildren")
//    public class DeleteMenuAndChildren {
//        @Test
//        public void shouldDeleteMenu_MenuItem_Crosswalk_Group_Allergen() {
//            Restaurant restaurant = Restaurant.builder()
//                    .name("Restaurant")
//                    .build();
//            restaurantRepo.saveAndFlush(restaurant);
//
//            Menu firstMenu = Menu.builder()
//                    .name("FirstMenu")
//                    .isActive(false)
//                    .isLinked(false)
//                    .build();
//            Menu secondMenu = Menu.builder()
//                    .name("SecondMenu")
//                    .isActive(false)
//                    .isLinked(false)
//                    .build();
//            menuRepo.saveAllAndFlush(asList(firstMenu, secondMenu));
//
//            RestaurantMenuCrosswalk firstCrosswalk = RestaurantMenuCrosswalk.builder()
//                    .menuId(firstMenu.getId())
//                    .restaurantId(restaurant.getId())
//                    .build();
//            RestaurantMenuCrosswalk secondCrosswalk = RestaurantMenuCrosswalk.builder()
//                    .menuId(secondMenu.getId())
//                    .restaurantId(restaurant.getId())
//                    .build();
//            restaurantMenuCrosswalkRepo.saveAllAndFlush(asList(firstCrosswalk, secondCrosswalk));
//
//            MenuItemGroup firstGroup = MenuItemGroup.builder()
//                    .name("FirstGroup")
//                    .position(0)
//                    .menuId(firstMenu.getId())
//                    .build();
//            MenuItemGroup secondGroup = MenuItemGroup.builder()
//                    .name("SecondGroup")
//                    .position(1)
//                    .menuId(firstMenu.getId())
//                    .build();
//            MenuItemGroup thirdGroup = MenuItemGroup.builder()
//                    .name("ThirdGroup")
//                    .position(0)
//                    .menuId(secondMenu.getId())
//                    .build();
//            menuItemGroupRepo.saveAllAndFlush(asList(firstGroup, secondGroup, thirdGroup));
//
//            MenuItem firstItem = MenuItem.builder()
//                    .menuId(firstMenu.getId())
//                    .menuItemGroupId(firstGroup.getId())
//                    .price(9.99)
//                    .name("FirstItem")
//                    .position(0)
//                    .build();
//            MenuItem secondItem = MenuItem.builder()
//                    .menuId(firstMenu.getId())
//                    .menuItemGroupId(firstGroup.getId())
//                    .price(6.00)
//                    .name("SecondItem")
//                    .position(1)
//                    .build();
//            MenuItem thirdItem = MenuItem.builder()
//                    .menuId(firstMenu.getId())
//                    .menuItemGroupId(secondGroup.getId())
//                    .price(2.54)
//                    .name("ThirdItem")
//                    .position(0)
//                    .build();
//            MenuItem fourthItem = MenuItem.builder()
//                    .menuId(secondMenu.getId())
//                    .menuItemGroupId(thirdGroup.getId())
//                    .price(1.50)
//                    .name("FourthItem")
//                    .position(0)
//                    .build();
//            menuItemRepo.saveAllAndFlush(asList(firstItem, secondItem, thirdItem, fourthItem));
//
//            MenuItemAllergen firstAllergen = MenuItemAllergen.builder()
//                    .menuItemId(firstItem.getId())
//                    .allergenId(2)
//                    .build();
//            MenuItemAllergen secondAllergen = MenuItemAllergen.builder()
//                    .menuItemId(firstItem.getId())
//                    .allergenId(5)
//                    .build();
//            MenuItemAllergen thirdAllergen = MenuItemAllergen.builder()
//                    .menuItemId(secondAllergen.getId())
//                    .allergenId(6)
//                    .build();
//            MenuItemAllergen fourthAllergen = MenuItemAllergen.builder()
//                    .menuItemId(fourthItem.getId())
//                    .allergenId(2)
//                    .build();
//            menuItemAllergenRepo.saveAllAndFlush(asList(firstAllergen, secondAllergen, thirdAllergen, fourthAllergen));
//
//            menuJdbcRepo.deleteMenuAndChildren(firstMenu.getId());
//
//            List<Menu> actualMenus = menuRepo.findAll();
//            List<RestaurantMenuCrosswalk> actualCrosswalks = restaurantMenuCrosswalkRepo.findAll();
//            List<MenuItemGroup> actualGroups = menuItemGroupRepo.findAll();
//            List<MenuItem> actualItems = menuItemRepo.findAll();
//            List<MenuItemAllergen> actualAllergens = menuItemAllergenRepo.findAll();
//
//            assertEquals(singletonList(secondMenu), actualMenus);
//            assertEquals(singletonList(secondCrosswalk), actualCrosswalks);
//            assertEquals(singletonList(thirdGroup), actualGroups);
//            assertEquals(singletonList(fourthItem), actualItems);
//            assertEquals(singletonList(fourthAllergen), actualAllergens);
//        }
//    }
}
