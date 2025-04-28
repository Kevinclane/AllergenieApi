package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.MenuItem;
import com.allergenie.api.Models.Responses.LoadedMenuResponse;
import com.allergenie.api.Services.MenuItemService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MenuItemControllerTests {
    @Mock
    private MenuItemService menuItemService;

    @InjectMocks
    private MenuItemController controller;

    @Nested
    @DisplayName("getMenuItems")
    public class GetMenuItems {
        @Test
        public void shouldReturnListOfMenuItems() {
            List<MenuItem> items = asList(
                    MenuItem.builder()
                            .id(5)
                            .build(),
                    MenuItem.builder()
                            .id(67)
                            .build()
            );
            when(menuItemService.getMenuItemsByMenuId(33))
                    .thenReturn(items);

            ResponseEntity<List<MenuItem>> actual = controller.getMenuItems(33);
            assertEquals(new ResponseEntity<>(items, HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            when(menuItemService.getMenuItemsByMenuId(any()))
                    .thenThrow();

            ResponseEntity<List<MenuItem>> actual = controller.getMenuItems(null);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }

    @Nested
    @DisplayName("getMenuItemDetails")
    public class GetMenuItemDetails {
        @Test
        public void shouldReturnLoadedMenuResponse() {
            LoadedMenuResponse response = LoadedMenuResponse.builder().build();
            when(menuItemService.getMenuItemsDetails(55))
                    .thenReturn(response);

            ResponseEntity<LoadedMenuResponse> actual = controller.getMenuItemsDetails(55);
            assertEquals(new ResponseEntity<>(response, HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            when(menuItemService.getMenuItemsDetails(any()))
                    .thenThrow();

            ResponseEntity<LoadedMenuResponse> actual = controller.getMenuItemsDetails(null);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }
}
