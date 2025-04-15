package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Services.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping(value = "/api/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/all")
    public ResponseEntity<List<Restaurant>> getRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantService.getRestaurants();
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(emptyList(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(Integer id) {
        try {
            Restaurant restaurant = restaurantService.getRestaurantById(id);
            return new ResponseEntity<>(restaurant, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Restaurant> saveRestaurant(@RequestBody Restaurant restaurant) {
        try {
            Restaurant newRestaurant = restaurantService.saveRestaurant(restaurant);
            return new ResponseEntity<>(newRestaurant, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Restaurant> deleteRestaurant(@PathVariable Integer id) {
        try {
            restaurantService.deleteRestaurant(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
