package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Models.Requests.NewEditMenuRequest;
import com.allergenie.api.Models.Responses.MenuDetailsResponse;
import com.allergenie.api.Models.Responses.MenuItemGroupDetails;
import com.allergenie.api.Services.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/all/{restaurantId}")
    public ResponseEntity<List<Menu>> getMenusByRestaurantId(@PathVariable Integer restaurantId) {
        try {
            List<Menu> response = menuService.getMenusByRestaurantId(restaurantId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Menu> createMenu(@RequestBody NewEditMenuRequest request) {
        try {
            Menu response = menuService.createMenu(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Menu> updateMenu(@RequestBody NewEditMenuRequest request) {
        try {
            Menu response = menuService.updateMenu(request);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/updateFullMenu")
    public ResponseEntity<List<MenuItemGroupDetails>> updateFullMenu(@RequestBody List<MenuItemGroupDetails> request) {
        try {
            List<MenuItemGroupDetails> responses = menuService.updateFullMenu(request);
            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getMenuDetails/{menuId}")
    public ResponseEntity<MenuDetailsResponse> getMenuDetails(@PathVariable Integer menuId) {
        try {
            MenuDetailsResponse response = menuService.getMenuDetails(menuId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{menuId}")
    public ResponseEntity<String> deleteMenu(@PathVariable Integer menuId) {
        try {
            if(menuId == 0) {
                return new ResponseEntity<>("Invalid menuId", HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>("You hit the API!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
