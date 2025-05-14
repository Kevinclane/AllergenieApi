package com.allergenie.api.Repos;

import com.allergenie.api.DatabaseUtils;
import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Models.Entities.RestaurantMenuCrosswalk;
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
public class MenuRepoTests {
    private DatabaseUtils databaseUtils;
    private RestaurantRepo restaurantRepo;
    private RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo;
    private MenuRepo menuRepo;

    @Autowired
    public MenuRepoTests(
            DatabaseUtils databaseUtils,
            RestaurantRepo restaurantRepo,
            RestaurantMenuCrosswalkRepo restaurantMenuCrosswalkRepo,
            MenuRepo menuRepo
    ) {
        this.databaseUtils = databaseUtils;
        this.restaurantRepo = restaurantRepo;
        this.restaurantMenuCrosswalkRepo = restaurantMenuCrosswalkRepo;
        this.menuRepo = menuRepo;
    }

    @BeforeEach
    private void setup() {
        databaseUtils.resetDatabase();
    }

    @Nested
    @DisplayName("findAllByRestaurantId")
    public class FindAllByRestaurantId {
        @Test
        public void shouldReturnListOfMenuIdsLinkedToRestaurantId() {
            Restaurant firstRestaurant = Restaurant.builder()
                    .name("FirstRestaurant")
                    .build();
            Restaurant secondRestaurant = Restaurant.builder()
                    .name("SecondRestaurant")
                    .build();
            restaurantRepo.saveAllAndFlush(asList(firstRestaurant, secondRestaurant));

            Menu firstMenu = Menu.builder()
                    .name("FirstMenu")
                    .build();
            Menu secondMenu = Menu.builder()
                    .name("SecondMenu")
                    .build();
            Menu thirdMenu = Menu.builder()
                    .name("ThirdMenu")
                    .build();
            Menu fourthMenu = Menu.builder()
                    .name("FourthMenu")
                    .build();
            menuRepo.saveAllAndFlush(asList(
                    firstMenu,
                    secondMenu,
                    thirdMenu,
                    fourthMenu
            ));

            RestaurantMenuCrosswalk firstCrosswalk = RestaurantMenuCrosswalk.builder()
                    .restaurantId(firstRestaurant.getId())
                    .menuId(firstMenu.getId())
                    .build();
            RestaurantMenuCrosswalk secondCrosswalk = RestaurantMenuCrosswalk.builder()
                    .restaurantId(firstRestaurant.getId())
                    .menuId(secondMenu.getId())
                    .build();
            RestaurantMenuCrosswalk thirdCrosswalk = RestaurantMenuCrosswalk.builder()
                    .restaurantId(firstRestaurant.getId())
                    .menuId(thirdMenu.getId())
                    .build();
            RestaurantMenuCrosswalk fourthCrosswalk = RestaurantMenuCrosswalk.builder()
                    .restaurantId(secondRestaurant.getId())
                    .menuId(fourthMenu.getId())
                    .build();
            restaurantMenuCrosswalkRepo.saveAllAndFlush(asList(
                    firstCrosswalk,
                    secondCrosswalk,
                    thirdCrosswalk,
                    fourthCrosswalk
            ));

            List<Integer> actual = menuRepo.findAllByRestaurantId(firstRestaurant.getId());
            assertEquals(asList(
                    firstMenu.getId(),
                    secondMenu.getId(),
                    thirdMenu.getId()
            ), actual);
        }
    }
}
