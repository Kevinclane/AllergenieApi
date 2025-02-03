package com.allergenie.api.Repos;

import com.allergenie.api.Models.Entities.MenuItemAllergen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemAllergenRepo extends JpaRepository<MenuItemAllergen, Integer> {
    @Query(value = "SELECT mia.* FROM menuitemallergen mia JOIN menuitem mi on mia.menuitemid = mi.id WHERE mi.menuid = ?1 ORDER BY mi.id",
        nativeQuery = true)
    List<MenuItemAllergen> findAllByMenuId(Integer menuId);

    List<MenuItemAllergen> findAllByMenuItemId(Integer menuItemId);

    @Modifying
    @Query(value = "DELETE menuitemallergen FROM menuitemallergen " +
            "JOIN menuitem mi on menuitemallergen.menuitemid = mi.id " +
            "WHERE mi.id NOT IN ?1 and mi.menuid = ?2",
        nativeQuery = true)
    void deleteUnusedMenuItemAllergens(List<Integer> menuItemIds, Integer menuId);
}
