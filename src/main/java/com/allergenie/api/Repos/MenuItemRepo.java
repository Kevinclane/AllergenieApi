package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepo extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByMenuId(Integer menuId);

    void deleteByIdIn(List<Integer> ids);

    @Query(value = """
                SELECT id
                FROM menu_item
                WHERE menu_id = :menuId
                AND id NOT IN (:existingMenuItemIds)
            """, nativeQuery = true)
    List<Integer> findMenuItemIdsToDelete(@Param("existingMenuItemIds") List<Integer> existingMenuItemIds,
                                          @Param("menuId") Integer menuId);
}
