package com.allergenie.api.Models.Entities;

import com.allergenie.api.Models.Rows.MenuItemAllergenGroupRow;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer menuId;
    private String name;
    private Integer position;

    public MenuItemGroup(MenuItemAllergenGroupRow row) {
        this.id = row.getGroupId();
        this.menuId = row.getMenuId();
        this.name = row.getGroupName();
        this.position = row. getGroupPosition();
    }
}
