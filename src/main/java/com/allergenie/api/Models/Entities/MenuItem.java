package com.allergenie.api.Models.Entities;

import com.allergenie.api.Models.Responses.MenuItemDetails;
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
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer menuId;
    private Integer menuItemGroupId;
    private String name;
    private String description;
    private String extraDetails;
    private Double price;
    private Integer position;

    public MenuItem(MenuItemDetails details) {
        this.id = details.getId() == 0 ? null : details.getId();
        this.menuId = details.getMenuId();
        this.menuItemGroupId = details.getMenuItemGroupId();
        this.name = details.getName();
        this.description = details.getDescription();
        this.extraDetails = details.getExtraDetails();
        this.price = details.getPrice();
        this.position = details.getPosition();
    }
}
