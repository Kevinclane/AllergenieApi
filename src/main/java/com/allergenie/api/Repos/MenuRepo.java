package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepo extends JpaRepository<Menu, Integer> {
    @Query(value = """
            SELECT m.id FROM menu m
            JOIN restaurant_menu_crosswalk rmc on m.id = rmc.menu_id
            JOIN restaurant r on rmc.restaurant_id = r.id
            WHERE r.id = :restaurantId
            """, nativeQuery = true)
    List<Integer> findAllByRestaurantId(@Param("restaurantId") Integer id);
}
