package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Repos.RestaurantJdbcRepo;
import com.allergenie.api.Repos.RestaurantRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTests {
    @Mock
    private RestaurantRepo restaurantRepo;
    @Mock
    private RestaurantJdbcRepo restaurantJdbcRepo;
    @Mock
    private MenuService menuService;

    @InjectMocks
    private RestaurantService service;

    @Nested
    @DisplayName("getRestaurants")
    public class GetRestaurants {
        @Test
        public void shouldReturnListOfRestaurants() {
            List<Restaurant> expected = asList(
                    Restaurant.builder()
                            .id(30)
                            .name("R1")
                            .details("R1Details")
                            .emailAddress("R1@123.com")
                            .streetAddress("321 Sesame St")
                            .state("IL")
                            .city("Muppetville")
                            .zipCode("55555")
                            .phoneNumber("2223334444")
                            .build(),
                    Restaurant.builder()
                            .id(31)
                            .name("R2")
                            .details("R2Details")
                            .emailAddress("R2@123.com")
                            .streetAddress("111 Sesame St")
                            .state("IL")
                            .city("Muppetville")
                            .zipCode("55555")
                            .phoneNumber("5551239999")
                            .build()
            );

            when(restaurantRepo.findAll())
                    .thenReturn(expected);

            List<Restaurant> actual = service.getRestaurants();

            assertEquals(expected, actual);
        }
    }

    @Nested
    @DisplayName("getRestaurantById")
    public class GetRestaurantById {

        @Nested
        @DisplayName("WhenRestaurantIdIsValid")
        public class WhenRestaurantIdIsValid {
            @Test
            public void shouldReturnRestaurant() {
                Restaurant expected = Restaurant.builder()
                        .id(30)
                        .name("R1")
                        .details("R1Details")
                        .emailAddress("R1@123.com")
                        .streetAddress("321 Sesame St")
                        .state("IL")
                        .city("Muppetville")
                        .zipCode("55555")
                        .phoneNumber("2223334444")
                        .build();

                when(restaurantRepo.findById(30))
                        .thenReturn(Optional.of(expected));

                Restaurant actual = service.getRestaurantById(30);

                assertEquals(expected, actual);
            }
        }

        @Nested
        @DisplayName("WhenRestaurantIdIsInvalid")
        public class WhenRestaurantIdIsInvalid {
            @Test
            public void shouldThrowException() {
                Exception exception = assertThrows(Exception.class, () -> {
                    service.getRestaurantById(22);
                });

                assertFalse(exception.getMessage().isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("saveRestaurant")
    public class SaveRestaurant {
        @Nested
        @DisplayName("WhenRestaurantFieldsAreValid")
        public class WhenRestaurantFieldsAreValid {
            @Nested
            @DisplayName("WhenRestaurantIdIsZero")
            public class WhenRestaurantIdIsZero {
                @Test
                public void shouldSaveWithNullId() throws Exception {
                    Restaurant restaurant = Restaurant.builder()
                            .id(0)
                            .name("Name")
                            .phoneNumber("1112223333")
                            .emailAddress("123@123.com")
                            .streetAddress("123 Sesame St")
                            .city("Muppetville")
                            .state("ID")
                            .zipCode("55555")
                            .build();
                    when(restaurantRepo.save(restaurant))
                            .thenReturn(restaurant);

                    Restaurant actual = service.saveRestaurant(restaurant);
                    assertEquals(restaurant, actual);
                    verify(restaurantRepo).save(restaurant);
                }
            }

        }

        @Nested
        @DisplayName("WhenRestaurantFieldsAreInvalid")
        public class WhenRestaurantFieldsAreInvalid {
            @Test
            public void shouldThrowException() throws Exception {
                Exception exception = assertThrows(Exception.class, () -> {
                    service.saveRestaurant(Restaurant.builder().build());
                });
                assertFalse(exception.getMessage().isEmpty());
            }
        }
    }

    @Nested
    @DisplayName("deleteRestaurant")
    public class DeleteRestaurant {
        @Test
        public void shouldCallRestaurantRepo() {
            service.deleteRestaurant(21);
            verify(restaurantRepo).deleteById(21);
        }
    }
}
