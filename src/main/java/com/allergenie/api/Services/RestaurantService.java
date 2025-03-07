package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Repos.RestaurantJdbRepo;
import com.allergenie.api.Repos.RestaurantRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
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

    public Restaurant saveRestaurant(Restaurant restaurant) throws Exception {
        if(restaurant.getId().equals(0)) {
            restaurant.setId(null);
        }
        if(restaurant.isValid()) {
            return restaurantRepo.save(restaurant);

        } else {
            throw new Exception("Restaurant contains invalid fields");
        }
    }

    public void deleteRestaurant(Integer id) {
        //add cascade delete
        restaurantRepo.deleteById(id);
    }
}
