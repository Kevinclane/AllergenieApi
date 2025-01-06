package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.Menu;
import com.allergenie.api.Services.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.emptyList;

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
            return new ResponseEntity<>(emptyList(), HttpStatus.BAD_REQUEST);
        }
    }

}