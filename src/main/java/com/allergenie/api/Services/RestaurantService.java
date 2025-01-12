package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Repos.RestaurantJdbRepo;
import com.allergenie.api.Repos.RestaurantRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

    private RestaurantRepo restaurantRepo;
    private RestaurantJdbRepo restaurantJdbRepo;

    public RestaurantService(
            RestaurantRepo restaurantRepo,
            RestaurantJdbRepo restaurantJdbRepo
    ) {
        this.restaurantRepo = restaurantRepo;
        this.restaurantJdbRepo = restaurantJdbRepo;
    }

    public List<Restaurant> getRestaurants() {
        List<Restaurant> restaurants = restaurantRepo.findAll();
        return restaurants;
    }


    public Optional<Restaurant> getRestaurantById(Integer id) {
        Optional<Restaurant> restaurant = restaurantRepo.findById(id);
        return restaurant;
    }

    public List<Restaurant> getByMenuId(Integer menuId) {
        List<Restaurant> restaurants = restaurantJdbRepo.getRestaurantsByMenuId(menuId);
        return restaurants;
    }
}
