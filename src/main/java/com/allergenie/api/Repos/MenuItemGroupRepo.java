package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.MenuItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemGroupRepo extends JpaRepository<MenuItemGroup, Integer> {
    @Modifying
    @Query(value = """
            DELETE FROM menu_item_group
            WHERE id IN (
                SELECT id
                FROM menu_item_group
                WHERE menu_id = ?2
                AND id NOT IN (?1)
                )
            """, nativeQuery = true)
    void deleteUnusedGroups(List<Integer> existingGroupIds, Integer menuId);
}
