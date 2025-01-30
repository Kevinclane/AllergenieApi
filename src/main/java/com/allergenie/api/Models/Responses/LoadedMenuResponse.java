package com.allergenie.api.Models.Responses;

import com.allergenie.api.Models.Entities.Allergen;
import lombok.Data;

import java.util.List;

@Data
public class LoadedMenuResponse {
    private List<MenuItemGroupDetails> groupedItems;
    private List<Allergen> allergens;
}
