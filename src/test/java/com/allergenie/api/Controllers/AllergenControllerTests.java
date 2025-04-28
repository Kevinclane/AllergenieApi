package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.Allergen;
import com.allergenie.api.Services.AllergenService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AllergenControllerTests {
    @Mock
    private AllergenService allergenService;

    @InjectMocks
    private AllergenController controller;

    @Nested
    @DisplayName("getAllergens")
    public class GetAllergens {
        @Test
        public void shouldReturnListOfAllergens() {
            List<Allergen> allergens = asList(
                    Allergen.builder()
                            .id(1)
                            .name("Milk")
                            .build(),
                    Allergen.builder()
                            .id(2)
                            .name("Eggs")
                            .build(),
                    Allergen.builder()
                            .id(3)
                            .name("Fish")
                            .build(),
                    Allergen.builder()
                            .id(4)
                            .name("Shellfish")
                            .build(),
                    Allergen.builder()
                            .id(5)
                            .name("Tree Nuts")
                            .build(),
                    Allergen.builder()
                            .id(6)
                            .name("Peanuts")
                            .build(),
                    Allergen.builder()
                            .id(7)
                            .name("Wheat")
                            .build(),
                    Allergen.builder()
                            .id(8)
                            .name("Soybeans")
                            .build(),
                    Allergen.builder()
                            .id(9)
                            .name("Sesame")
                            .build()
            );

            when(allergenService.getAllergens())
                    .thenReturn(allergens);

            ResponseEntity<List<Allergen>> expected = new ResponseEntity<>(allergens, HttpStatus.OK);
            ResponseEntity<List<Allergen>> actual = controller.getAllergens();
            assertEquals(expected, actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            when(allergenService.getAllergens())
                    .thenThrow();

            ResponseEntity<List<Allergen>> actual = controller.getAllergens();
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }

    }

}
