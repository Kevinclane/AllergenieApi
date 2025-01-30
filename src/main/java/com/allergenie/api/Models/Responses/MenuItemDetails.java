package com.allergenie.api.Models.Responses;

import com.allergenie.api.Models.Entities.Allergen;
import com.allergenie.api.Models.Rows.MenuItemGroupRow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDetails {
    private Integer id;
    private Integer menuId;
    private Integer menuItemGroupId;
    private String name;
    private String description;
    private String extraDetails;
    private Double price;
    private Integer position;
    private List<Allergen> allergens;

    public MenuItemDetails(MenuItemGroupRow row) {
        this.id = row.getMenuItemId();
        this.menuId = row.getMenuId();
        this.menuItemGroupId = row.getGroupId();
        this.name = row.getMenuItemName();
        this.description = row.getMenuItemDescription();
        this.extraDetails = row.getMenuItemExtraDetails();
        this.price = row.getMenuItemPrice();
        this.position = row.getMenuItemPosition();
    }
}
