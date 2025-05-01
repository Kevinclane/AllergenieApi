package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.MenuItemAllergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemAllergenRepo extends JpaRepository<MenuItemAllergen, Integer> {
    void deleteByIdIn(List<Integer> ids);
    @Query(value = "SELECT mia.* FROM menu_item_allergen mia JOIN menu_item mi on mia.menu_item_id = mi.id WHERE mi.menu_id = ?1 ORDER BY mi.id",
            nativeQuery = true)
    List<MenuItemAllergen> findAllByMenuId(Integer menuId);

    List<MenuItemAllergen> findAllByMenuItemId(Integer menuItemId);

    @Query(value = """
                SELECT mia.id
                FROM menu_item_allergen mia
                LEFT JOIN menu_item mi on mia.menu_item_id = mi.id
                WHERE mi.menu_id = :menuId
                AND mi.id NOT IN (:existingMenuItemIds)
            """, nativeQuery = true)
    List<Integer> findMenuItemAllergenIdsToDelete(@Param("existingMenuItemIds") List<Integer> existingMenuItemIds,
                                          @Param("menuId") Integer menuId);
}
