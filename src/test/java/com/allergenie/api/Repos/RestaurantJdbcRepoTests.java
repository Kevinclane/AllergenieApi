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
public class RestaurantJdbcRepoTests {
    private DatabaseUtils databaseUtils;
    private MenuRepo menuRepo;
    private RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo;
    private RestaurantRepo restaurantRepo;
    private RestaurantJdbcRepo restaurantJdbcRepo;

    @Autowired
    public RestaurantJdbcRepoTests(DatabaseUtils databaseUtils, MenuRepo menuRepo, RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo, RestaurantRepo restaurantRepo, RestaurantJdbcRepo restaurantJdbcRepo) {
        this.databaseUtils = databaseUtils;
        this.menuRepo = menuRepo;
        this.restaurantMenuCrosswalkRepo = restaurantMenuCrosswalkRepo;
        this.restaurantRepo = restaurantRepo;
        this.restaurantJdbcRepo = restaurantJdbcRepo;
    }

    @BeforeEach
    public void setup() {
        databaseUtils.resetDatabase();
    }

    @Nested
    @DisplayName("getRestaurantsByMenuId")
    public class GetRestaurantsByMenuId {
        @Test
        public void shouldReturnRestaurantsLinkedToProvidedMenuId() {
            Menu firstMenu = Menu.builder()
                    .name("FirstMenu")
                    .build();
            Menu secondMenu = Menu.builder()
                    .name("SecondMenu")
                    .build();
            menuRepo.saveAllAndFlush(asList(firstMenu, secondMenu));

            Restaurant firstRestaurant = Restaurant.builder()
                    .name("FirstRestaurant")
                    .build();
            Restaurant secondRestaurant = Restaurant.builder()
                    .name("SecondRestaurant")
                    .build();
            Restaurant thirdRestaurant = Restaurant.builder()
                    .name("ThirdRestaurant")
                    .build();
            restaurantRepo.saveAllAndFlush(asList(firstRestaurant, secondRestaurant, thirdRestaurant));

            RestaurantMenuCrosswalk firstCrosswalk = RestaurantMenuCrosswalk.builder()
                    .restaurantId(firstRestaurant.getId())
                    .menuId(firstMenu.getId())
                    .build();
            RestaurantMenuCrosswalk secondCrosswalk = RestaurantMenuCrosswalk.builder()
                    .restaurantId(secondRestaurant.getId())
                    .menuId(firstMenu.getId())
                    .build();
            RestaurantMenuCrosswalk thirdCrosswalk = RestaurantMenuCrosswalk.builder()
                    .restaurantId(thirdRestaurant.getId())
                    .menuId(secondMenu.getId())
                    .build();
            restaurantMenuCrosswalkRepo.saveAllAndFlush(asList(firstCrosswalk, secondCrosswalk, thirdCrosswalk));

            List<Restaurant> expected = asList(firstRestaurant, secondRestaurant);
            List<Restaurant> actual = restaurantJdbcRepo.getRestaurantsByMenuId(firstMenu.getId());
            assertEquals(expected, actual);
        }
    }

}
