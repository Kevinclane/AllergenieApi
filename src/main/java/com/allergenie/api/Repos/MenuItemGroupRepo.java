package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.MenuItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemGroupRepo extends JpaRepository<MenuItemGroup, Integer> {
    List<MenuItemGroup> findAllByMenuId(Integer menuId);

    @Modifying
    @Query(value = "DELETE menu_item_group FROM menu_item_group WHERE id NOT IN ?1 AND menu_id = ?2",
            nativeQuery = true)
    void deleteUnusedGroups(List<Integer> existingGroupIds, Integer menuId);
}
