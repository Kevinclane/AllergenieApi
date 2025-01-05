package com.allergenie.api.Services;

import com.allergenie.api.Models.Entities.Restaurant;
import com.allergenie.api.Models.Responses.TempResponse;
import com.allergenie.api.Repos.RestaurantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepo restaurantRepo;

    public List<TempResponse> getRestaurants() {
        List<Restaurant> restaurants = restaurantRepo.findAll();
        List<TempResponse> responses = new ArrayList<>();
        for(Restaurant restaurant : restaurants) {
            TempResponse response = new TempResponse();
            response.setId(restaurant.getId());
            response.setName(restaurant.getName());
            response.setStreetAddress(restaurant.getStreetAddress());
            response.setCity(restaurant.getCity());
            response.setZipCode(restaurant.getZipCode());
            response.setPhoneNumber(restaurant.getPhoneNumber());
            responses.add(response);
        }

        return responses;
    }


}
