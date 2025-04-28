package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByMenuId(Integer menuId);

    @Modifying
    @Query(value = """
                DELETE FROM menu_item
                WHERE id IN (
                    SELECT mi.id
                    FROM menu_item mi
                    WHERE mi.menu_id = ?2
                    AND mi.id NOT IN (?1)
                )
            """, nativeQuery = true)
    void deleteUnusedMenuItems(List<Integer> existingMenuItemIds, Integer menuId);
}
