package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Models.Responses.TempResponse;
import com.allergenie.api.Services.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.Collections.emptyList;

@RestController
@RequestMapping(value = "/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/all")
    public ResponseEntity<List<TempResponse>> getRestaurants() {
        try {
            List<TempResponse> restaurants = restaurantService.getRestaurants();
            return new ResponseEntity<>(restaurants, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(emptyList(), HttpStatus.BAD_REQUEST);
        }
    }

}
