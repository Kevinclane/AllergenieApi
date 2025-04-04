package com.allergenie.api.Models.Responses;

import com.allergenie.api.Models.Rows.MenuItemGroupRow;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemGroupDetails {
    private Integer id;
    private Integer menuId;
    private String name;
    private Integer position;
    private List<MenuItemDetails> menuItems;

    public MenuItemGroupDetails(MenuItemGroupRow row, List<MenuItemDetails> menuItems) {
        this.id = row.getGroupId();
        this.menuId = row.getMenuId();
        this.name = row.getGroupName();
        this.position = row.getGroupPosition();
        this.menuItems = menuItems;
    }
}
