package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.RestaurantMenuCrosswalk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantMenuCrosswalkRepo extends JpaRepository<RestaurantMenuCrosswalk, Integer> {
    List<RestaurantMenuCrosswalk> findByMenuId(Integer id);
}
