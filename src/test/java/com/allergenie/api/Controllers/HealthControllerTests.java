package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Repos.RestaurantRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HealthControllerTests {
    @Mock
    private RestaurantRepo restaurantRepo;

    @InjectMocks
    private HealthController controller;

    @Test
    public void ping_shouldReturnOK() {
        ResponseEntity<String> actual = controller.ping();
        assertEquals(new ResponseEntity<>("pong", HttpStatus.OK), actual);
    }

    @Nested
    @DisplayName("dbPing")
    public class DbPing {
        @Test
        public void shouldReturnOk() {
            when(restaurantRepo.findById(1))
                    .thenReturn(Optional.of(Restaurant.builder().build()));
            ResponseEntity<String> actual = controller.dbPing();
            assertEquals(new ResponseEntity<>("Connected to Database", HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            when(restaurantRepo.findById(1))
                    .thenThrow();
            ResponseEntity<String> actual = controller.dbPing();
            assertEquals(new ResponseEntity<>("Error connecting to Database", HttpStatus.BAD_REQUEST), actual);
        }
    }
}
