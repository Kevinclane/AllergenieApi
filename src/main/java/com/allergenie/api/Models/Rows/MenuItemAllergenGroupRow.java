package com.allergenie.api.Models.Rows;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemAllergenGroupRow {
    private Integer menuItemId;
    private Integer menuId;
    private String menuItemName;
    private String menuItemDescription;
    private String menuItemExtraDetails;
    private Double menuItemPrice;
    private Integer menuItemPosition;
    private Integer groupId;
    private String groupName;
    private Integer groupPosition;
    private Integer menuItemAllergenId;
    private Integer allergenId;

    public MenuItemAllergenGroupRow(ResultSet rs) throws SQLException {
        this.menuItemId = rs.getInt("menuItemId");
        this.menuId = rs.getInt("menuId");
        this.menuItemName = rs.getString("menuItemName");
        this.menuItemDescription = rs.getString("menuItemDescription");
        this.menuItemExtraDetails = rs.getString("menuItemExtraDetails");
        this.menuItemPrice = rs.getDouble("menuItemPrice");
        this.menuItemPosition = rs.getInt("menuItemPosition");
        this.groupId = rs.getInt("groupId");
        this.groupName = rs.getString("groupName");
        this.groupPosition = rs.getInt("groupPosition");
        this.menuItemAllergenId = rs.getInt("menuItemAllergenId");
        this.allergenId = rs.getInt("allergenId");
    }
}
