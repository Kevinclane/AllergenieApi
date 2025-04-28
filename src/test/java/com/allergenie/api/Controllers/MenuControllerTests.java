package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Models.Requests.NewEditMenuRequest;
import com.allergenie.api.Models.Responses.MenuDetailsResponse;
import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
import com.allergenie.api.Models.Responses.MessageResponse;
import com.allergenie.api.Services.MenuService;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuControllerTests {
    @Mock
    private MenuService menuService;

    @InjectMocks
    private MenuController controller;

    @Nested
    @DisplayName("getMenusByRestaurantId")
    public class GetMenusByRestaurantId {
        @Test
        public void shouldReturnListOfMenus() {
            List<Menu> menus = asList(
                    Menu.builder()
                            .id(2)
                            .build(),
                    Menu.builder()
                            .id(5)
                            .build()
            );
            when(menuService.getMenusByRestaurantId(2))
                    .thenReturn(menus);
            ResponseEntity<List<Menu>> actual = controller.getMenusByRestaurantId(2);
            assertEquals(new ResponseEntity<>(menus, HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            when(menuService.getMenusByRestaurantId(2))
                    .thenThrow();
            ResponseEntity<List<Menu>> actual = controller.getMenusByRestaurantId(2);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }

    @Nested
    @DisplayName("createMenu")
    public class CreateMenu {
        @Test
        public void shouldReturnMenu() {
            NewEditMenuRequest request = NewEditMenuRequest.builder()
                    .id(2)
                    .build();
            Menu menu = Menu.builder()
                    .id(2)
                    .build();

            when(menuService.createMenu(request))
                    .thenReturn(menu);
            ResponseEntity<Menu> actual = controller.createMenu(request);
            assertEquals(new ResponseEntity<>(menu, HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            when(menuService.createMenu(any()))
                    .thenThrow();
            ResponseEntity<Menu> actual = controller.createMenu(null);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }

    @Nested
    @DisplayName("updateMenu")
    public class UpdateMenu {
        @Test
        public void shouldReturnMenu() throws Exception {
            NewEditMenuRequest request = NewEditMenuRequest.builder()
                    .id(34)
                    .build();
            Menu menu = Menu.builder()
                    .id(34)
                    .build();

            when(menuService.updateMenu(request))
                    .thenReturn(menu);
            ResponseEntity<Menu> actual = controller.updateMenu(request);
            assertEquals(new ResponseEntity<>(menu, HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() throws Exception {
            when(menuService.updateMenu(any()))
                    .thenThrow();

            ResponseEntity<Menu> actual = controller.updateMenu(null);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }

    @Nested
    @DisplayName("updateFullMenu")
    public class UpdateFullMenu {
        @Test
        public void shouldReturnListOfMenuItemGroupDetails() {
            List<MenuItemGroupDetails> details = asList(
                    MenuItemGroupDetails.builder()
                            .id(1)
                            .menuId(2)
                            .build(),
                    MenuItemGroupDetails.builder()
                            .id(4)
                            .menuId(7)
                            .build()
            );

            when(menuService.updateMenuContents(details))
                    .thenReturn(details);

            ResponseEntity<List<MenuItemGroupDetails>> actual = controller.updateMenuContents(details);
            assertEquals(new ResponseEntity<>(details, HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            when(menuService.updateMenuContents(any()))
                    .thenThrow();
            ResponseEntity<List<MenuItemGroupDetails>> actual = controller.updateMenuContents(null);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }

    @Nested
    @DisplayName("getMenuDetails")
    public class GetMenuDetails {
        @Test
        public void shouldReturnMenuDetailsResponse() {
            MenuDetailsResponse response = MenuDetailsResponse.builder()
                    .id(55)
                    .build();
            when(menuService.getMenuDetails(55))
                    .thenReturn(response);
            ResponseEntity<MenuDetailsResponse> actual = controller.getMenuDetails(55);
            assertEquals(new ResponseEntity<>(response, HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            when(menuService.getMenuDetails(any()))
                    .thenThrow();
            ResponseEntity<MenuDetailsResponse> actual = controller.getMenuDetails(null);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }

    @Nested
    @DisplayName("deleteMenu")
    public class DeleteMenu {
        @Test
        public void shouldReturnOK() {
            ResponseEntity<MessageResponse> actual = controller.deleteMenu(55);
            assertEquals(new ResponseEntity<>(new MessageResponse("Successfully deleted menu"), HttpStatus.OK), actual);
        }

        @Test
        public void whenExceptionIsThrown_shouldReturnBadRequest() {
            doThrow(new RuntimeException("test"))
                    .when(menuService)
                    .deleteMenuById(55);

            ResponseEntity<MessageResponse> actual = controller.deleteMenu(55);
            assertEquals(new ResponseEntity<>(HttpStatus.BAD_REQUEST), actual);
        }
    }
}
