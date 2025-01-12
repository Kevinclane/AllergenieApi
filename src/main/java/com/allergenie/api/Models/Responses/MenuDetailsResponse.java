package com.allergenie.api.Models.Responses;

import com.allergenie.api.Models.Entities.Restaurant;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class MenuDetailsResponse {
    private Integer id;
    private String name;
    private Boolean isActive;
    private List<Restaurant> linkedRestaurants;
    private List<Restaurant> allRestaurants;
}
