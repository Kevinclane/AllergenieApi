package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.MenuItemAllergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemAllergenRepo extends JpaRepository<MenuItemAllergen, Integer> {
    @Query(value = "SELECT mia.* FROM menu_item_allergen mia JOIN menu_item mi on mia.menu_item_id = mi.id WHERE mi.menu_id = ?1 ORDER BY mi.id",
            nativeQuery = true)
    List<MenuItemAllergen> findAllByMenuId(Integer menuId);

    List<MenuItemAllergen> findAllByMenuItemId(Integer menuItemId);

    @Modifying
    @Query(value = """
                DELETE FROM menu_item_allergen
                WHERE menu_item_id IN (
                    SELECT mi.id
                    FROM menu_item mi
                    WHERE mi.menu_id = ?2
                    AND mi.id NOT IN (?1)
                )
            """, nativeQuery = true)
    void deleteUnusedMenuItemAllergens(List<Integer> menuItemIds, Integer menuId);
}
