package com.allergenie.api.Controllers;

import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Repos.RestaurantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(value = "/health")
public class HealthController {

    @Autowired
    private RestaurantRepo restaurantRepo;
    @GetMapping(value = "ping")
    public ResponseEntity<String> ping() {
        return new ResponseEntity<>("pong", HttpStatus.OK);
    }

    @GetMapping(value = "db")
    public ResponseEntity<String> dbPing() {
        try {
            Optional<Restaurant> restaurant = restaurantRepo.findById(1);
            return new ResponseEntity<>("Connected to Database", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error connecting to Database", HttpStatus.BAD_REQUEST);
        }
    }
}
