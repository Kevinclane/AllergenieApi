package com.allergenie.api.Models.Responses;

import com.allergenie.api.Models.Entities.Allergen;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoadedMenuResponse {
    private List<MenuItemGroupDetails> groupedItems;
    private List<Allergen> allergens;
}
