package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.MenuItem;
import com.allergenie.api.Models.Responses.LoadedMenuResponse;
import com.allergenie.api.Services.MenuItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/menuItem")
public class MenuItemController {
    @Autowired
    private MenuItemService menuItemService;

    @GetMapping("/all/{menuId}")
    public ResponseEntity<List<MenuItem>> getMenuItems(@PathVariable Integer menuId) {
        try {
            List<MenuItem> menuItems = menuItemService.getMenuItems(menuId);
            return new ResponseEntity<>(menuItems, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/details/{menuId}")
    public ResponseEntity<LoadedMenuResponse> getMenuItemsDetails(@PathVariable Integer menuId) {
        try {
            LoadedMenuResponse response = menuItemService.getMenuItemsDetails(menuId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
