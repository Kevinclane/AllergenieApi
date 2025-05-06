package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.MenuItemGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemGroupRepo extends JpaRepository<MenuItemGroup, Integer> {
    void deleteByIdIn(List<Integer> ids);
    @Query(value = """
                SELECT id
                FROM menu_item_group
                WHERE menu_id = :menuId
                AND id NOT IN (:existingGroupIds)
            """, nativeQuery = true)
    List<Integer> findGroupIdsToDelete(@Param("existingGroupIds") List<Integer> existingGroupIds,
                                          @Param("menuId") Integer menuId);
}
