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
    @Query(value = "DELETE menuitemgroup FROM menuitemgroup WHERE id NOT IN ?1 AND menuid = ?2",
            nativeQuery = true)
    void deleteUnusedGroups(List<Integer> existingGroupIds, Integer menuId);
}
