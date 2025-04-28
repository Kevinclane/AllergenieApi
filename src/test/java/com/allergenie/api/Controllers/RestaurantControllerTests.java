package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Models.Responses.MessageResponse;
import com.allergenie.api.Services.RestaurantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantControllerTests {
    @Mock
    private RestaurantService restaurantService;

    @InjectMocks
    private RestaurantController controller;

    @Nested
    @DisplayName("getRestaurants")
    public class GetRestaurants {
        @Test
        public void shouldReturnListOfRestaurants() {
            List<Restaurant> restaurants = asList(
                    Restaurant.builder()
                            .id(5)
                            .build(),
                    Restaurant.builder()
                            .id(13)
                            .build()
            );

            when(restaurantService.getRestaurants())
                    .thenReturn(restaurants);

            ResponseEntity<List<Restaurant>> actual = controller.getRestaurants();
            assertEquals(new ResponseEntity<>(restaurants, HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            when(restaurantService.getRestaurants())
                    .thenThrow();

            ResponseEntity<List<Restaurant>> actual = controller.getRestaurants();
            assertEquals(new ResponseEntity<>(emptyList(), HttpStatus.BAD_REQUEST), actual);
        }
    }

    @Nested
    @DisplayName("getRestaurantById")
    public class GetRestaurantById {
        @Test
        public void shouldReturnRestaurant() {
            Restaurant restaurant = Restaurant.builder()
                    .id(11)
                    .build();
            when(restaurantService.getRestaurantById(11))
                    .thenReturn(restaurant);

            ResponseEntity<Restaurant> actual = controller.getRestaurantById(11);
            assertEquals(new ResponseEntity<>(restaurant, HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            when(restaurantService.getRestaurantById(any()))
                    .thenThrow();

            ResponseEntity<Restaurant> actual = controller.getRestaurantById(null);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }

    @Nested
    @DisplayName("saveRestaurant")
    public class SaveRestaurant {
        @Test
        public void shouldReturnRestaurant() throws Exception {
            Restaurant restaurant = Restaurant.builder()
                    .id(11)
                    .build();
            when(restaurantService.saveRestaurant(restaurant))
                    .thenReturn(restaurant);

            ResponseEntity<Restaurant> actual = controller.saveRestaurant(restaurant);
            assertEquals(new ResponseEntity<>(restaurant, HttpStatus.CREATED), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() throws Exception {
            when(restaurantService.saveRestaurant(any()))
                    .thenThrow();
            ResponseEntity<Restaurant> actual = controller.saveRestaurant(null);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }

    @Nested
    @DisplayName("deleteRestaurant")
    public class DeleteRestaurant {
        @Test
        public void shouldReturnOK() {
            ResponseEntity<MessageResponse> actual = controller.deleteRestaurant(11);
            assertEquals(new ResponseEntity<>(new MessageResponse("Successfully deleted restaurant"), HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            doThrow(new RuntimeException("test"))
                    .when(restaurantService)
                    .deleteRestaurant(11);

            ResponseEntity<MessageResponse> actual = controller.deleteRestaurant(11);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }
}
